// $Id: HarmonicsPath.cc 2862 2007-12-06 23:12:59Z flaterco $

/*  HarmonicsPath  Vector of harmonics file names as specified by environment.

    Copyright (C) 1997  David Flater.

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


#ifdef UseSemicolonPathsep
static const char pathSeparator (';');
#else
static const char pathSeparator (':');
#endif

#ifdef UseLocalFiles
static const char confFile[] = "xtide.conf";
#else
static const char confFile[] = "/etc/xtide.conf";
#endif


HarmonicsPath::HarmonicsPath ():
  _noPathProvided(false) {

  // Get HFILE_PATH from environment or /etc/xtide.conf
  Dstr hfile_path (getenv ("HFILE_PATH"));
  if (hfile_path.isNull()) {
    FILE *configfile;
    if ((configfile = fopen (confFile, "rb"))) {
      hfile_path.getline (configfile);
      fclose (configfile);
      // Kluge for MS-DOS line discipline
      if (hfile_path.back() == '\r')
        hfile_path -= hfile_path.length() - 1;
    }
  }
  _origPath = hfile_path;
  if (hfile_path.isNull()) {
    _noPathProvided = true;
    hfile_path = "harmonics.tcd";
  }

  // Trying to tolerate whitespace in file names.
  while (hfile_path.length()) {
    int i = hfile_path.strchr (pathSeparator);
    if (i == 0)
      hfile_path /= 1;
    else if (i < 0) {
      push_back (hfile_path);
      return;
    } else {
      Dstr temp (hfile_path);
      temp -= i;
      push_back (temp);
      hfile_path /= i+1;
    }
  }
}


const bool HarmonicsPath::noPathProvided() {
  return _noPathProvided;
}


constString HarmonicsPath::origPath() {
  return _origPath.aschar();
}

// Cleanup2006 Done
