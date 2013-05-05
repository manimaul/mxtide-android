// $Id: TideEventsOrganizer.cc 2641 2007-09-02 21:31:02Z flaterco $

// TideEventsOrganizer  Collect, sort, subset, and iterate over tide events.

/*
    Copyright (C) 2004  David Flater.

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


void TideEventsOrganizer::add (const TideEvent &tideEvent) {
  Timestamp t (tideEvent.eventTime);
  TideEventsIterator it (lower_bound (t - Global::eventSafetyMarginMinus1));
  if (it != end()) {
    TideEventsIterator stop
                           (upper_bound (t + Global::eventSafetyMarginMinus1));
    // or end()
    while (it != stop) {
      if (tideEvent.eventType == it->second.eventType)
        return;
      ++it;
    }
  }
  insert (std::pair<const Timestamp, TideEvent> (t, tideEvent));
}

// Cleanup2006 Done
