// $Id: Global.cc 2946 2008-01-18 23:12:25Z flaterco $

/*  Global  Global variables and functions.

    Copyright (C) 1998  David Flater.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#include "common.hh"
#include "HarmonicsPath.hh"
#include <locale.h>
#include <sys/stat.h>
#include <limits>       // No relation to limits.h

//#ifdef HAVE_LANGINFO_H
//#include <langinfo.h>
//#endif

#ifdef HAVE_DIRENT_H
#include <dirent.h>
#endif


// eventPrecisionJD should be static within Skycal, and
// eventSafetyMarginMinus1 should be static within
// TideEventsOrganizer, but they have to be here to avoid the "static
// initialization order fiasco."

const Interval Global::zeroInterval (0);
const Interval Global::hour (HOURSECONDS);
const Interval Global::day (DAYSECONDS);
const Interval Global::eventPrecision (15);
const double Global::eventPrecisionJD (Global::eventPrecision / Global::day);
const Interval Global::eventSafetyMargin (60);
const Interval Global::eventSafetyMarginMinus1 (Global::eventSafetyMargin - 1);
const Interval Global::defaultPredictInterval (345600);
const Interval Global::tidalDay (Global::intervalround (
						360.0*HOURSECONDS/14.4920521));
const Interval Global::halfCycle (Global::intervalround (
						360.0*HOURSECONDS/57.9682084));
const double Global::aspectMagicNumber (56160.0); // Matches XTide 1.
const unsigned Global::minGraphWidth (64U);
const unsigned Global::minGraphHeight (64U);
// If minTTYwidth and minTTYheight are different it will break Banner.
const unsigned Global::minTTYwidth (10U);
const unsigned Global::minTTYheight (10U);
const char Global::CSV_repchar ('|');


// Determination of year limits for time control dialogs.

// The current practice of harmonics-dwf is to include years 1700 to
// 2100 because the constituents are calibrated for 1900.

// dialogFirstYear should be 1700 or the earliest full year supported
// by time_t, whichever is later.

// dialogLastYear should be 2100 or the latest full year supported by
// time_t, whichever is earlier.

// Following are the standard requirements on time_t:
// 1.  time_t is an integer or real-floating type.  (SUSv3 sys/types.h)
// 2.  time_t represents time in seconds since the Epoch.  (SUSv3 time())
// 3.  The Epoch is 1970-01-01 00:00 UTC.  (SUSv3 definitions)

// Any real-floating representation should have sufficient range to
// cover 1700 to 2100.  You lose precision if you get too far from the
// Epoch, but the exponent gives you plenty of range.

// N.B., Timestamp.cc assumes that time_t is integral.

// Visual C++, as usual, is a special case:  time_t is int64_t, but
// the library's time functions fail for negative values and for year
// 3000 or later.  The limits calculated below are not used in the
// command-line client, so we don't worry about Visual C++.

// The following is slightly risky in that time_t might use a data
// type that is an extension to the ISO C++ standard, and a conforming
// but stupid implementation might not supply a corresponding
// specialization of numeric_limits.

typedef std::numeric_limits<time_t>  tt_limits;
#define tt_signed    tt_limits::is_signed
#define tt_smallint  tt_limits::is_integer && tt_limits::digits <= 32

const unsigned Global::dialogFirstYear
  (tt_signed ? tt_smallint ? 1902U : 1700U : 1970U);

const unsigned Global::dialogLastYear
  (tt_signed && tt_smallint ? 2037U : 2100U);


Settings Global::settings;
Dstr Global::codeset;
//FILE *Global::PNGFile = NULL;

static bool _disclaimerDisabled;
static Dstr disclaimerFileName;
static StationIndex *_stationIndex = NULL;
static bool daemonMode = false;
static void (*_errorCallback) (const Dstr &errorMessage,
                               Error::ErrType fatality) = NULL;



// Don't put newlines in this; see iCalendar use in Station.cc.
void Global::versionString (Dstr &version_out) {
  version_out = PACKAGE_STRING;
#ifdef NDEBUG
  version_out += " NDEBUG";
#endif
#ifdef TIME_WORKAROUND
  version_out += " TIME_WORKAROUND";
#endif
}


static void initDisclaimer() {
  static bool disclaimerInit = false;
  if (!disclaimerInit) {
    disclaimerInit = true;
#ifdef UseLocalFiles
    disclaimerFileName = ".disableXTidedisclaimer";
#else
    disclaimerFileName = getenv ("HOME");
    if (disclaimerFileName.isNull()) {
      _disclaimerDisabled = false;
      return;
    }
    disclaimerFileName += "/.disableXTidedisclaimer";
#endif
    struct stat buf;
    _disclaimerDisabled = (stat (disclaimerFileName.aschar(), &buf) == 0);
  }
}


void Global::initCodeset() {
  if (codeset.isNull()) {
//#ifdef HAVE_LANGINFO_H
//    // Method suggested by man utf-8 to obtain ambient codeset.
//    setlocale (LC_CTYPE, "");
//    codeset = nl_langinfo (CODESET);
//
//    // Undo damage so that libc functions won't change behavior in
//    // mysterious ways.
//    setlocale (LC_CTYPE, "C");
//#else
#ifdef _MSC_VER
    // Visual C++ land (code by Leonid Tochinski)
    codeset = setlocale (LC_CTYPE, "English_United States.1252");
#else
    codeset = "UTF-8";
    //codeset = "ISO-8859-1";
#endif
//#endif
  }
}


const bool Global::disclaimerDisabled() {
  initDisclaimer();
  return _disclaimerDisabled;
}


void Global::disableDisclaimer() {
  initDisclaimer();
  if (disclaimerFileName.isNull())
    Global::barf (Error::NOHOMEDIR);
  FILE *fp = fopen (disclaimerFileName.aschar(), "wb");
  if (!fp) {
    cantOpenFile (disclaimerFileName, Error::nonfatal);
  } else {
    fclose (fp);
    _disclaimerDisabled = true;
  }
}


StationIndex &Global::stationIndex () {
  if (!_stationIndex) {
    HarmonicsPath harmonicsPath;
    _stationIndex = new StationIndex();
    for (unsigned i=0; i<harmonicsPath.size(); ++i) {
      struct stat s;
      if (stat (harmonicsPath[i].aschar(), &s) == 0) {
        if (S_ISDIR (s.st_mode)) {
          Dstr dname (harmonicsPath[i]);
          dname += '/';
          DIR *dirp = opendir (dname.aschar());
          if (!dirp)
            xperror (dname.aschar());
          else {
            dirent *dp;
	    for (dp = readdir(dirp); dp != NULL; dp = readdir(dirp)) {
              Dstr fname (dp->d_name);
	      if (fname[0] == '.') // Skip all hidden files
		continue;
	      else {
                fname *= dname;
                _stationIndex->addHarmonicsFile (fname);
              }
	    }
	    closedir(dirp);
          }
        } else
          _stationIndex->addHarmonicsFile (harmonicsPath[i]);
      } else
        xperror (harmonicsPath[i].aschar());
    }
    if (_stationIndex->empty()) {
      if (harmonicsPath.noPathProvided())
        Global::barf (Error::NO_HFILE_PATH);
      else
	Global::barf (Error::NO_HFILE_IN_PATH, harmonicsPath.origPath());
      // Ignore the stupid case where the file exists but contains no
      // stations.
    }
    _stationIndex->sort();
    _stationIndex->setRootStationIndexIndices();
  }
  return *_stationIndex;
}


void Global::setDaemonMode() {
#ifdef HAVE_SYSLOG_H
  daemonMode = true;
  openlog ("xttpd", LOG_CONS|LOG_PID, LOG_DAEMON);
#else
  Global::barf (Error::NO_SYSLOG);
#endif
}


void Global::setErrorCallback (void (*errorCallback) (const Dstr &errorMessage,
						    Error::ErrType fatality)) {
  _errorCallback = errorCallback;
}


void Global::xperror (constCharPointer s) {
#ifdef HAVE_SYSLOG_H
  if (daemonMode)
    syslog (LOG_ERR, "%s: %s", s, strerror (errno));
  else
#endif
    perror (s);
}


void Global::log (const Dstr &message, int priority) {
  log (message.aschar(), priority);
}


void Global::log (constCharPointer message, int priority) {
  if (message) {
#ifdef HAVE_SYSLOG_H
    if (daemonMode)
      syslog (priority, "%s", message);
    else
#endif
      fprintf (stderr, "%s\n", message);
  }
}


void Global::log (constCharPointer message,
                  const Dstr &details,
                  int priority) {
  Dstr temp (message);
  temp += details;
  log (temp, priority);
}


static void errorMessage (Error::TideError err,
                          const Dstr &details,
		          Error::ErrType fatality,
                          Dstr &text_out) {
  if (fatality == Error::fatal)
    text_out = "XTide Fatal Error:  ";
  else
    text_out = "XTide Error:  ";
  switch (err) {
  case Error::YEAR_OUT_OF_RANGE:
    text_out += "YEAR_OUT_OF_RANGE\n\
Some operation has attempted to access time before 1 A.D. or after\n\
4000 A.D.  There's no point even trying to predict tides over that\n\
span of time.";
    break;
  case Error::MKTIME_FAILED:
    text_out += "MKTIME_FAILED\n\
XTide was unable to convert an informal time specification into a\n\
legal Unix timestamp.  This probably means that there is something\n\
wrong with the informal time specification, such as referring to a\n\
point in time that does not exist (like February 30th).  It may also\n\
mean that the requested time is outside of the Unix epoch, which only\n\
reliably includes the years from 1970 through 2037.  For the workaround,\n\
see http://www.flaterco.com/xtide/time_t.html.";
    break;
  case Error::TIMESTAMP_OVERFLOW:
    text_out += "TIMESTAMP_OVERFLOW\n\
A timestamp operation resulted in a value that was outside of the\n\
supported epoch.  The Unix epoch only reliably includes the years from\n\
1970 through 2037.  For the workaround, see\n\
http://www.flaterco.com/xtide/time_t.html.";
    break;
  case Error::YEAR_NOT_IN_TABLE:
    text_out += "YEAR_NOT_IN_TABLE\n\
Some operation has been initiated that needs data for a year that is\n\
not supported by the harmonics file being used.";
    break;
  case Error::NO_HFILE_PATH:
    text_out += "NO_HFILE_PATH\n\
If /etc/xtide.conf is not provided, you must set the environment variable\n\
HFILE_PATH to point to your harmonics files.  Example:\n\
  export HFILE_PATH=/usr/local/share/xtide/harmonics.tcd\n\
Please refer to the documentation for usage of /etc/xtide.conf.";
    break;
  case Error::NO_HFILE_IN_PATH:
    text_out += "NO_HFILE_IN_PATH\n\
A search path for harmonics files was provided, but no harmonics files were\n\
found anywhere in that path.  You might need to download a harmonics file\n\
from http://www.flaterco.com/xtide/files.html.";
    break;
  case Error::IMPOSSIBLE_CONVERSION:
    text_out += "IMPOSSIBLE_CONVERSION\n\
An attempt was made to convert between units of fundamentally different types,\n\
e.g., to convert from units of velocity to units of length.";
    break;
  case Error::NO_CONVERSION:
    text_out += "NO_CONVERSION\n\
An attempt was made to convert a measure with units to the same units.  This\n\
may indicate a fault in the program logic.";
    break;
  case Error::UNRECOGNIZED_UNITS:
    text_out += "UNRECOGNIZED_UNITS\n\
The units of a prediction value (e.g., feet, meters, knots) were not one of\n\
the recognized alternatives.";
    break;
  case Error::BOGUS_COORDINATES:
    text_out += "BOGUS_COORDINATES\n\
A latitude and longitude pair was found to be out of range.";
    break;
  case Error::CANT_OPEN_FILE:
    text_out += "CANT_OPEN_FILE\n\
Unable to open a file.";
    break;
  case Error::CORRUPT_HARMONICS_FILE:
    text_out += "CORRUPT_HARMONICS_FILE\n\
Your harmonics file does not conform to the required format.  This can be\n\
caused by:\n\
   --  updates to the harmonics file while xtide is running;\n\
   --  a corrupt harmonics file;\n\
   --  libtcd errors (which would have been reported to stderr).\n\
If the harmonics file has been updated, just restart xtide and it will be fine.";
    break;
  case Error::BADCOLORSPEC:
    text_out += "BADCOLORSPEC\n\
A color specification could not be parsed.";
    break;
  case Error::XPM_ERROR:
    text_out += "XPM_ERROR\n\
An error condition was reported by an Xpm library function.";
    break;
  case Error::NOHOMEDIR:
    text_out += "NOHOMEDIR\n\
The environment variable HOME is not set.";
    break;
  case Error::XMLPARSE:
    text_out += "XMLPARSE\n\
The XML file is ill-formed or exceeds the limitations of XTide's parser.";
    break;
  case Error::STATION_NOT_FOUND:
    text_out += "STATION_NOT_FOUND\n\
The specified station was not found in any harmonics file.";
    break;
  case Error::BADHHMM:
    text_out += "BADHHMM\n\
XTide was expecting an interval specification of the form [-]HH:MM where HH\n\
is hours and MM is minutes.  What it got did not parse.  This indicates a\n\
problem with an offset, meridian, or step value specification.";
    break;
  case Error::CANTOPENDISPLAY:
    text_out += "CANTOPENDISPLAY\n\
XTide cannot open your X11 display.  Check the setting of the\n\
DISPLAY environment variable, and check your permissions (xhost,\n\
xauth, kerberos, firewall, etc.).";
    break;
  case Error::NOT_A_NUMBER:
    text_out += "NOT_A_NUMBER\n\
Couldn't convert a text string to a number.";
    break;
  case Error::PNG_WRITE_FAILURE:
    text_out += "PNG_WRITE_FAILURE\n\
A general fatal error occurred while producing a PNG.";
    break;
  case Error::CANT_GET_SOCKET:
    text_out += "CANT_GET_SOCKET\n\
Xttpd was unable to bind its socket.  Common causes are (1) you tried to\n\
use the default port 80 without having root privileges; fix this by\n\
providing a usable port number (e.g., 8080) as the first command-line\n\
argument, or (2) there is already something running on the specified port,\n\
such as another web server.";
    break;
  case Error::ABSURD_OFFSETS:
    text_out += "ABSURD_OFFSETS\n\
A subordinate station's offsets were so nonsensical as to cause\n\
operational failures in XTide.";
    break;
  case Error::NUMBER_RANGE_ERROR:
    text_out += "NUMBER_RANGE_ERROR\n\
A number was parsed OK, but it is not in the range of acceptable values.";
    break;
  case Error::BAD_MODE:
    text_out += "BAD_MODE\n\
A mode specified with the -m command line switch is not supported.";
    break;
  case Error::BAD_FORMAT:
    text_out += "BAD_FORMAT\n\
The format requested with the -f command line switch is not supported for\n\
the selected mode.  The currently supported combinations of mode and format\n\
are as folows:\n\
\n\
	 MODE\n\
      abcCgklmprs\n\
  F c   X    XXX\n\
  O h X XX  X\n\
  R i   X\n\
  M l   XX\n\
  A p     XX\n\
  T t XXXXXXXXXXX";
    break;
  case Error::BAD_EVENTMASK:
    text_out += "BAD_EVENTMASK\n\
An eventmask may contain any combination of the letters p (phase of moon),\n\
S (sunrise), s (sunset), M (moonrise), and m (moonset), or it may be x\n\
(suppress none).  An eventmask was specified that did not conform to that.";
    break;
  case Error::BAD_TIMESTAMP:
    text_out += "BAD_TIMESTAMP\n\
The -b and -e command line switches expect timestamps to be in the format\n\
\"YYYY-MM-DD HH:MM\".  Example:  tide -b \"1998-01-01 13:00\"";
    break;
  case Error::BAD_IP_ADDRESS:
    text_out += "BAD_IP_ADDRESS\n\
The IP address given to xttpd was not a valid IPv4 dotted-decimal string.";
    break;
  case Error::BAD_BOOL:
    text_out += "BAD_BOOL\n\
A boolean parameter was set to some value other than 'y' or 'n'.";
    break;
  case Error::BAD_TEXT:
    text_out += "BAD_TEXT\n\
A text parameter had something not right about it.";
    break;
  case Error::BAD_OR_AMBIGUOUS_COMMAND_LINE:
    text_out += "BAD_OR_AMBIGUOUS_COMMAND_LINE\n\
The command line could not be rationalized.  Probably you have provided an\n\
unrecognized switch or invalid argument.  However, you can also get this\n\
error by using ambiguous syntax.  For example, the shorthand -lw5 could mean\n\
\"set the line width to 5\" (-lw 5) or it could mean \"load the location\n\
named w5\" (-l w5).";
    break;
  case Error::CANT_LOAD_FONT:
    text_out += "CANT_LOAD_FONT\n\
A font that is required by XTide does not appear on the system.\n\
Perhaps there is an optional font package that you need to install.";
    break;
  case Error::NO_SYSLOG:
    text_out += "NO_SYSLOG\n\
This platform has no syslog facility, so xttpd cannot run as a daemon.";
    break;
  default:
    assert (false);
  }
  text_out += '\n';
  if (!(details.isNull())) {
    text_out += "\nError details:\n";
    text_out += details;
    text_out += '\n';
  }
}


void Global::barf (Error::TideError err,
                   const Dstr &details,
                   Error::ErrType fatality) {
  static bool snakeBit (false); // Hide double-barfs.
  Dstr message;
  errorMessage (err, details, fatality, message);
  if (!snakeBit)
    log (message, LOG_ERR);
  if (fatality == Error::fatal)
    snakeBit = true;
  if (_errorCallback)
    (*_errorCallback) (message, fatality);
  if (fatality == Error::fatal)
    exit (-1);
}


void Global::barf (Error::TideError err, Error::ErrType fatality) {
  barf (err, Dstr(), fatality);
}


void Global::formatBarf (Mode::Mode mode, Format::Format form) {
  Dstr details ("Can't do format ");
  details += (char)form;
  details += " in mode ";
  details += (char)mode;
  details += '.';
  barf (Error::BAD_FORMAT, details);
}


const Global::GetDoubleReturn Global::getDouble (const Dstr &number,
                                   Configurable::Interpretation interpretation,
                                                 double &val_out) {
  assert (interpretation == Configurable::posDoubleInterp ||
          interpretation == Configurable::nonnegativeDoubleInterp ||
          interpretation == Configurable::numberInterp);
  if (number.length() > 0) {
    if (number.strchr ('\n') != -1 ||
        number.strchr ('\r') != -1 ||
        number.strchr (' ') != -1) {
      Dstr details ("Numbers aren't supposed to contain whitespace.  You entered '");
      details += number;
      details += "'.";
      barf (Error::NOT_A_NUMBER, details, Error::nonfatal);
      return inputNotOK;
    } else {
      double temp;
      if (sscanf (number.aschar(), "%lf", &temp) != 1) {
	Dstr details ("The offending input was '");
	details += number;
	details += "'.";
	barf (Error::NOT_A_NUMBER, details, Error::nonfatal);
        return inputNotOK;
      } else {
        if ((temp  < 0.0 && interpretation != Configurable::numberInterp) ||
            (temp == 0.0 && interpretation == Configurable::posDoubleInterp)) {
  	  Dstr details ("The offending input was '");
	  details += number;
	  details += "'.";
	  barf (Error::NUMBER_RANGE_ERROR, details, Error::nonfatal);
          return inputNotOK;
        } else {
          val_out = temp;
  	  return inputOK;
        }
      }
    }
  }
  return emptyInput;
}


const bool Global::isValidEventMask (const Dstr &eventMask) {
  static constString legalEventMasks = "pSsMm";
  if (eventMask.length() < 1)
    return false;
  if (eventMask == "x")
    return true;
  for (unsigned i=0; i<eventMask.length(); ++i)
    if (!strchr (legalEventMasks, eventMask[i]))
      return false;
  return true;
}


void Global::cantOpenFile (const Dstr &filename,
                           Error::ErrType fatality) {
  Dstr details (filename);
  details += ": ";
  details += strerror (errno);
  details += '.';
  barf (Error::CANT_OPEN_FILE, details, fatality);
}


void Global::cant_mktime (const Dstr &timeString,
                          const Dstr &timezone,
                          Error::ErrType fatality) {
  Dstr details ("The offending input was ");
  details += timeString;
  details += "\nin the time zone ";
  if (settings["z"].c == 'n')
    details += timezone;
  else
    details += "UTC0";
  barf (Error::MKTIME_FAILED, details, fatality);
}


const int Global::iround (double x) {
  return (int) floor (x+0.5);
}


const interval_rep_t Global::intervalround (double x) {
  return (interval_rep_t) floor (x+0.5);
}

// Cleanup2006 Done
