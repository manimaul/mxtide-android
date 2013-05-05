// $Id: CurrentBearing.hh 2641 2007-09-02 21:31:02Z flaterco $

/*
    CurrentBearing:  Store and print <int>° or <int>° true, or null.

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

// The redundancy between CurrentBearing and Angle is illusory.  XTide
// never needs to do anything with current directions except store
// them and print them in degrees.  They can't even be floats (libtcd
// ships ints).  However, they can be null.

class CurrentBearing: public Nullable {
public:

  CurrentBearing (); // Creates a null CurrentBearing.

  // Units must be degrees.  Value must be between 0 and 359.  Set the
  // bool if they are "degrees true."

  // AFAIK the only alternative is "degrees relative," which would be
  // useless, but, whatever.

  CurrentBearing (uint16_t degrees, bool isTrue);

  // Print value in degrees with a degrees symbol and possibly a
  // "true" qualifier attached.  This does not have fixed field width.
  // It is an error to attempt to print a null CurrentBearing.
  void print (Dstr &text_out) const;

protected:
  uint16_t _degrees;
  bool isDegreesTrue;
};

// Cleanup2006 Done
