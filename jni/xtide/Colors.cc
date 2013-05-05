// $Id: Colors.cc 2641 2007-09-02 21:31:02Z flaterco $

/*  Colors  Manage XTide colors without X.

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
#include "rgb.hh"


constString Colors::colorarg[numColors] = {
  "bg", "fg", "mc", "bc", "dc", "nc", "fc", "ec", "Dc", "Mc"
};


const bool Colors::parseColor (const Dstr &colorName,
			       uint8_t &r,
			       uint8_t &g,
			       uint8_t &b,
			       Error::ErrType fatality) {

  r = g = b = 0;
  constString fmt1 = "rgb:%" SCNx8 "/%" SCNx8 "/%" SCNx8;

  if (sscanf (colorName.aschar(), fmt1, &r, &g, &b) != 3) {

    r = g = b = 0;
    constString fmt2 = "%" SCNx8;

    // Kludge for default fg and bg colors under the CDE
    if (colorName[0] == '#' && colorName.length() == 13) {
      char temp[3];
      temp[2] = '\0';
      temp[0] = colorName[1];
      temp[1] = colorName[2];
      sscanf (temp, fmt2, &r);
      temp[0] = colorName[5];
      temp[1] = colorName[6];
      sscanf (temp, fmt2, &g);
      temp[0] = colorName[9];
      temp[1] = colorName[10];
      sscanf (temp, fmt2, &b);

    // Kludge for default fg and bg colors under Debian
    } else if (colorName[0] == '#' && colorName.length() == 7) {
      char temp[3];
      temp[2] = '\0';
      temp[0] = colorName[1];
      temp[1] = colorName[2];
      sscanf (temp, fmt2, &r);
      temp[0] = colorName[3];
      temp[1] = colorName[4];
      sscanf (temp, fmt2, &g);
      temp[0] = colorName[5];
      temp[1] = colorName[6];
      sscanf (temp, fmt2, &b);

    } else {
      for (unsigned rr=0; rgbtxt[rr].name; ++rr)
	if (dstrcasecmp (colorName, rgbtxt[rr].name) == 0) {
	  r = rgbtxt[rr].r;
	  g = rgbtxt[rr].g;
	  b = rgbtxt[rr].b;
	  return true;
	}
      Dstr details ("The offending color spec was ");
      details += colorName;
      details += '.';
      Global::barf (Error::BADCOLORSPEC, details, fatality);
      return false;
    }
  }

  return true;
}

// Cleanup2006 Done
