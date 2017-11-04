// $Id: RGBGraph.cc 5748 2014-10-11 19:38:53Z flaterco $

/*  RGBGraph  Graph implemented as raw RGB image.

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

#include "libxtide.hh"
#include "Graph.hh"
#include "PixelatedGraph.hh"
#include "RGBGraph.hh"

namespace libxtide {


RGBGraph::RGBGraph (unsigned xSize, unsigned ySize, GraphStyle style):
  PixelatedGraph (xSize, ySize, style) {
  assert (xSize >= Global::minGraphWidth && ySize >= Global::minGraphHeight);
  rgb.resize (xSize * ySize * 3);
  for (unsigned a=0; a<Colors::numColors; ++a)
    Colors::parseColor (Global::settings[Colors::colorarg[a]].s,
                        cmap[a][0],
                        cmap[a][1],
                        cmap[a][2]);
}


const unsigned RGBGraph::stringWidth (const Dstr &s) const {
  return libxtide::stringWidth (Global::graphFont, s);
}


const unsigned RGBGraph::fontHeight() const {
  return Global::graphFont.height;
}


const unsigned RGBGraph::oughtHeight() const {
  return Global::graphFont.oughtHeight;
}


const unsigned RGBGraph::oughtVerticalMargin() const {
  return 1;
}


void RGBGraph::setPixel (int x, int y, Colors::Colorchoice c) {
  assert (c < (int)Colors::numColors);
  if (x < 0 || x >= (int)_xSize || y < 0 || y >= (int)_ySize)
    return;
  SafeVector<unsigned char>::iterator it = rgb.begin() + (y * _xSize + x) * 3;
  *it     = cmap[c][0];
  *(++it) = cmap[c][1];
  *(++it) = cmap[c][2];
}


void RGBGraph::setPixel (int x,
                         int y,
                         Colors::Colorchoice c,
                         double opacity) {
  assert (c < (int)Colors::numColors);
  if (x < 0 || x >= (int)_xSize || y < 0 || y >= (int)_ySize)
    return;
  SafeVector<unsigned char>::iterator it = rgb.begin() + (y * _xSize + x) * 3;
  *it = linterp (*it, cmap[c][0], opacity);
  ++it;
  *it = linterp (*it, cmap[c][1], opacity);
  ++it;
  *it = linterp (*it, cmap[c][2], opacity);
}


void RGBGraph::drawStringP (int x, int y, const Dstr &s) {
  for (unsigned a=0; a<s.length(); ++a) {
    const ClientSide::Glyph &g (Global::graphFont.glyphs[(uint8_t)s[a]]);
    for (SafeVector<ClientSide::Pixel>::const_iterator it (g.pixels.begin());
	 it != g.pixels.end(); ++it)
      setPixel (x+it->x, y+it->y, Colors::foreground, it->opacity / 255.0);
    x += g.advance;
  }
}

}
