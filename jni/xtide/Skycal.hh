// $Id: Skycal.hh 2098 2007-02-25 22:51:38Z flaterco $

// Skycal.hh -- Functions for sun and moon events.
// Please see Skycal.cc for verbose commentary.

// Prediction of moon phases, sun and moon rises and sets has nothing
// to do with tide prediction.  There is no overlap between this code
// and the tide prediction code.

namespace Skycal {

  // eventTime and eventType are set to the next moon phase event
  // following time t.  Nothing else in tideEvent_out is changed.
  void findNextMoonPhase (Timestamp t, TideEvent &tideEvent_out);

  // eventTime and eventType are set to the next (sun/moon) rise or
  // set event following time t.  Nothing else in tideEvent_out is
  // changed.
  enum RiseSetType {solar, lunar};
  void findNextRiseOrSet (Timestamp t,
                          const Coordinates &c,
                          RiseSetType riseSetType,
                          TideEvent &tideEvent_out);

  // Returns true if sun is up at time t.
  const bool sunIsUp (Timestamp t, const Coordinates &c);

}

// Cleanup2006 Done
