LOCAL_PATH := $(call my-dir)

LIBXTIDE_SRC= \
	xtide/Amplitude.cc \
	xtide/Angle.cc \
	xtide/Colors.cc \
	xtide/Constituent.cc \
	xtide/ConstituentSet.cc \
	xtide/Coordinates.cc \
	xtide/CurrentBearing.cc \
	xtide/Date.cc \
	xtide/Dstr.cc \
	xtide/Global.cc \
	xtide/HarmonicsFile.cc \
	xtide/HarmonicsPath.cc \
	xtide/Interval.cc \
	xtide/MetaField.cc \
	xtide/Nullable.cc \
	xtide/NullableInterval.cc \
	xtide/NullablePredictionValue.cc \
	xtide/Offsets.cc \
	xtide/PredictionValue.cc \
	xtide/Settings.cc \
	xtide/Skycal.cc \
	xtide/Speed.cc \
	xtide/Station.cc \
	xtide/StationIndex.cc \
	xtide/StationRef.cc \
	xtide/SubordinateStation.cc \
	xtide/tide.cc \
	xtide/TideEvent.cc \
	xtide/TideEventsOrganizer.cc \
	xtide/Timestamp.cc \
	xtide/Units.cc \
	xtide/xml_l.cc \
	xtide/xml_y.cc \
	xtide/Year.cc \
	xtide/ZoneIndex.cc 


include $(CLEAR_VARS)

LOCAL_CPP_EXTENSION 	:= .cc
LOCAL_MODULE    		:= libxtide
LOCAL_CFLAGS    		:= -O2 -Ijni/xtide -Wno-missing-field-initializers
LOCAL_SRC_FILES 		:= $(LIBXTIDE_SRC)
LOCAL_STATIC_LIBRARIES	:= cpufeatures

include $(BUILD_STATIC_LIBRARY)