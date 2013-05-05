// $Id: config.hh 2840 2007-12-01 03:49:48Z flaterco $

// Compiled-in defaults for settings.

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

// This file is included ONLY by Settings.cc.  All other modules
// obtain the values of configurables via Global::settings.

// The order of precedence, from least significant to most
// significant, is:
//    1.  config.hh
//    2.  Xdefaults (X resources)
//    3.  ~/.xtide.xml (Control Panel)
//    4.  Command line
// Note that only xtide (not xttpd or tide) reads Xdefaults.

// Default colors.
static constString bgdefcolor     = "white";
static constString fgdefcolor     = "black";
static constString markdefcolor   = "red";
static constString buttondefcolor = "gray80";
static constString daydefcolor    = "SkyBlue";
static constString nightdefcolor  = "DeepSkyBlue";
static constString flooddefcolor  = "Blue";
static constString ebbdefcolor    = "SeaGreen";
static constString datumdefcolor  = "white";
static constString msldefcolor    = "yellow";

// Default graph width and height (pixels), and aspect
static const unsigned defgwidth  = 960U;
static const unsigned defgheight = 312U;
static const double   defgaspect = 1.0;

// Default clock width (pixels).
static const unsigned defcwidth = 84U;

// Default width of ASCII graphs and banners (characters).
static const unsigned defttywidth = 79U;

// Default height of ASCII graphs (characters).
static const unsigned defttyheight = 24U;

// Default length and width of paper in LaTeX output (mm).  This need
// not match your actual paper; use "Shrink oversized pages" in print
// options.
static const double defpageheight = 420.0;
static const double defpagewidth  = 297.0;
static const double defpagemargin =  10.0;

// Anti-alias tide graphs on true color displays?
static const char antialias = 'y';

// Label tenths of units in tide graphs?
static const char graphtenths = 'n';

// Draw datum and middle-level lines?
static const char extralines = 'n';

// Prefer flat map to round globe location chooser?
static const char flatearth = 'n';

// Create tide clocks with buttons?
static const char cbuttons = 'n';

// Draw depth lines on top of graph?
static const char toplines = 'n';

// Draw tide graph as a line graph?
static const char nofill = 'n';

// Events to suppress (p = phase of moon, S = sunrise, s = sunset, M =
// moonrise, m = moonset), or x to suppress none.
static constString eventmask = "x";

// Infer constituents?  (Expert use only)
static const char infer = 'n';

// Default width of lines in line graphs
static const double deflwidth = 2.5;

// Default preferred units:  ft, m, or x (no preference).
static constString prefunits = "x";

// Force UTC?
static const char forceZuluTime = 'n';

// Date, time, hour formats.

// For US-style AM/PM
static constString datefmt = "%Y-%m-%d";
#ifdef HAVE_GOOD_STRFTIME
static constString hourfmt = "%l";
static constString timefmt = "%l:%M %p %Z";
#else
static constString hourfmt = "%I";
static constString timefmt = "%I:%M %p %Z";
#endif

// For 24-hour time with no AM/PM, use
//   hourfmt "%H"
//   timefmt "%H:%M %Z"
// See the man page for strftime to learn how to alter the formats
// to do other things.

// Default center longitude for location chooser.
// Valid values:  -180 -150 -120 -90 -60 -30 0 30 60 90 120 150 360
// 360 will pick the longitude with the most tide stations.
static const double defgl = 360.0;

// Cleanup2006 Done
