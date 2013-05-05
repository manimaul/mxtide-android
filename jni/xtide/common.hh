// $Id: common.hh 2833 2007-12-01 01:27:02Z flaterco $
	
// Global includes for xtide, tide, and xttpd.

/*  Copyright (C) 2007  David Flater.

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
#define PACKAGE_NAME "XTide" 
#define PACKAGE_TARNAME "xtide"
#define PACKAGE_VERSION "2.10"
#define PACKAGE_STRING "XTide 2.10"
#define PACKAGE_BUGREPORT "dave@flaterco.com"
#define PACKAGE "xtide"
#define VERSION "2.10"
#define DSTDC_HEADERS 1
#define HAVE_SYS_TYPES_H 1
#define HAVE_SYS_STAT_H 1
#define HAVE_STDLIB_H 1
#define HAVE_STRING_H 1
#define HAVE_MEMORY_H 1
#define HAVE_STRINGS_H 1
#define HAVE_INTTYPES_H 1
#define HAVE_STDINT_H 1
#define HAVE_UNISTD_H 1
#define HAVE_LIBTCD 1
#define HAVE_SYSLOG_H 1
#define HAVE_LANGINFO_H 0
#define HAVE_DIRENT_H 1
#define HAVE_SYS_RESOURCE_H 1
#define HAVE_LLROUND 1
#define HAVE_GOOD_STRFTIME 1



// Need to define this to get inttypes.h to define the macros.
// SUSV3 says nothing about it; intttypes.h blames C99.
#define __STDC_FORMAT_MACROS

// Under Visual C++ 2008 Express Edition, this is needed to get M_PI etc.
#define _USE_MATH_DEFINES

#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <sys/types.h>

#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif

#ifdef HAVE_IO_H
#include <io.h>
#endif

#if HAVE_INTTYPES_H
#include <inttypes.h>
#elif HAVE_STDINT_H
#include <stdint.h>
#endif

// If we still didn't get the macros, take a guess.
#ifndef SCNx8
#define SCNx8 "hhx"
#endif


/*
  "Inevitably, the programmed validity checks of the defensive
  programming approach will result in run-time overheads and, where
  performance demands are critical, many checks are often removed from
  the operational software; their use is restricted to the testing
  phase where they can identify the misuse of components by faulty
  designs.  In the context of producing complex systems which can
  never be fully tested, this tendency to remove the protection
  afforded by programmed validity checks is most regrettable and is
  not recommended here." ---M. R. Moulding, "Designing for high
  integrity:  The software fault tolerance approach," Section 3.4.  In
  C. T. Sennett, ed., High-Integrity Software, Plenum Press, New York
  and London, 1989.
*/
#ifdef NDEBUG
#ifdef USE_PRAGMA_MESSAGE
#pragma message("WARNING:  NDEBUG is defined.  This configuration is unsupported and discouraged.")
#else
#warning NDEBUG is defined.  This configuration is unsupported and discouraged.
#endif
#endif

// In cases where the program relies on side-effects from what would
// be an assert expression, the following macro is used to ensure that
// the expression will be evaluated even if asserts are disabled.
#ifndef require
#define require(expr) {       \
  bool requireExpr ((expr));  \
  assert (requireExpr);       \
}
#endif


#ifdef UseGnuAttributes

#define unusedParameter  __attribute__ ((unused))
#define warnUnusedResult __attribute__ ((warn_unused_result))

#else

#define unusedParameter
#define warnUnusedResult

#endif


#include <assert.h>
#include <string.h>
#include <time.h>
#include <ctype.h>
#include <errno.h>
#include <limits.h>

#ifdef HAVE_SYSLOG_H
#include <syslog.h>
#else
// Syslog will not be called, but the following must be stubbed out.
#define LOG_ERR     3
#define LOG_WARNING 4
#define LOG_NOTICE  5
#define LOG_INFO    6
#endif


#include <set>
#include <list>
#include <algorithm>
#include <memory>
#include "SafeVector.hh"
#include "BetterMap.hh"

// This must be done before including the XTide headers.
// The new configure script promises that we will have an int64_t.
// (YAY.)
#ifdef TIME_WORKAROUND
#define time_t int64_t
#endif

// A date is encoded as Year * 10000 + Month [1, 12] * 100 + Day [1, 31].
typedef unsigned long date_t;

// Since XTide now roams outside of the minimal 1970 to 2037 epoch,
// 32 bits no longer suffice.
typedef int64_t interval_rep_t;

typedef char const *                constCharPointer;
typedef char const * const          constString;
typedef char const * const *        constStringPointer;
typedef char const * const * const  constStringArray;


#if HAVE_LIBDSTR
#include <Dstr>
#else
#include "Dstr.hh"
#endif

#include "ModeFormat.hh"
#include "Units.hh"
#include "PredictionValue.hh"
#include "Configurable.hh"
#include "Errors.hh"
#include "Global.hh"
#include "Nullable.hh"
#include "MetaField.hh"
#include "NullablePredictionValue.hh"
#include "Colors.hh"
#include "Settings.hh"
#include "Amplitude.hh"
#include "Year.hh"
#include "Angle.hh"
#include "CurrentBearing.hh"
#include "Speed.hh"
#include "Interval.hh"
#include "NullableInterval.hh"
#include "Offsets.hh"
#include "Timestamp.hh"
#include "Date.hh"
#include "Coordinates.hh"
#include "StationRef.hh"
#include "Constituent.hh"
#include "ConstituentSet.hh"
#include "StationIndex.hh"
#include "TideEvent.hh"
#include "TideEventsOrganizer.hh"
#include "Station.hh"


#define DAYSECONDS 86400
#define HOURSECONDS 3600

// Cleanup2006 Done
