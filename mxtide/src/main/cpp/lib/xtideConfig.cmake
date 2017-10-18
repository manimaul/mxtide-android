cmake_minimum_required(VERSION 3.4.1)

set(png_DIR ${CMAKE_CURRENT_LIST_DIR})
find_package(png REQUIRED)

set(tcd_DIR ${CMAKE_CURRENT_LIST_DIR})
find_package(tcd REQUIRED)

set(XTIDE_SRC_DIR ${CMAKE_CURRENT_LIST_DIR}/xtide)

message(XTIDE_SRC_DIR: ${XTIDE_SRC_DIR})

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O2 -Wno-missing-field-initializers -Wdeprecated-register -Wformat -Wimplicit-function-declaration")


add_library(xtide STATIC
        ${XTIDE_SRC_DIR}/Amplitude.cc
        ${XTIDE_SRC_DIR}/Angle.cc
        ${XTIDE_SRC_DIR}/Banner.cc
        ${XTIDE_SRC_DIR}/Calendar.cc
        ${XTIDE_SRC_DIR}/CalendarFormC.cc
        ${XTIDE_SRC_DIR}/CalendarFormH.cc
        ${XTIDE_SRC_DIR}/CalendarFormL.cc
        ${XTIDE_SRC_DIR}/CalendarFormNotC.cc
        ${XTIDE_SRC_DIR}/CalendarFormT.cc
        ${XTIDE_SRC_DIR}/ClientSideFont.cc
        ${XTIDE_SRC_DIR}/Colors.cc
        ${XTIDE_SRC_DIR}/Constituent.cc
        ${XTIDE_SRC_DIR}/ConstituentSet.cc
        ${XTIDE_SRC_DIR}/Coordinates.cc
        ${XTIDE_SRC_DIR}/CurrentBearing.cc
        ${XTIDE_SRC_DIR}/Date.cc
        ${XTIDE_SRC_DIR}/Dstr.cc
        ${XTIDE_SRC_DIR}/Global.cc
        ${XTIDE_SRC_DIR}/Graph.cc
        ${XTIDE_SRC_DIR}/HarmonicsFile.cc
        ${XTIDE_SRC_DIR}/HarmonicsPath.cc
        ${XTIDE_SRC_DIR}/Interval.cc
        ${XTIDE_SRC_DIR}/MetaField.cc
        ${XTIDE_SRC_DIR}/Nullable.cc
        ${XTIDE_SRC_DIR}/NullableInterval.cc
        ${XTIDE_SRC_DIR}/NullablePredictionValue.cc
        ${XTIDE_SRC_DIR}/Offsets.cc
        ${XTIDE_SRC_DIR}/PixelatedGraph.cc
        ${XTIDE_SRC_DIR}/PredictionValue.cc
        ${XTIDE_SRC_DIR}/RGBGraph.cc
        ${XTIDE_SRC_DIR}/Settings.cc
        ${XTIDE_SRC_DIR}/Skycal.cc
        ${XTIDE_SRC_DIR}/Speed.cc
        ${XTIDE_SRC_DIR}/Station.cc
        ${XTIDE_SRC_DIR}/StationIndex.cc
        ${XTIDE_SRC_DIR}/StationRef.cc
        ${XTIDE_SRC_DIR}/SubordinateStation.cc
        ${XTIDE_SRC_DIR}/SVGGraph.cc
        ${XTIDE_SRC_DIR}/tide.cc
        ${XTIDE_SRC_DIR}/TideEvent.cc
        ${XTIDE_SRC_DIR}/TideEventsOrganizer.cc
        ${XTIDE_SRC_DIR}/Timestamp.cc
        ${XTIDE_SRC_DIR}/TTYGraph.cc
        ${XTIDE_SRC_DIR}/Units.cc
        ${XTIDE_SRC_DIR}/xml_l.cc
        ${XTIDE_SRC_DIR}/xml_y.cc
        ${XTIDE_SRC_DIR}/Year.cc
        ${XTIDE_SRC_DIR}/ZoneIndex.cc
)

#set_target_properties(xtide PROPERTIES LINKER_LANGUAGE CXX)
target_include_directories(xtide PUBLIC ${CMAKE_CURRENT_LIST_DIR})
target_link_libraries(xtide png tcd)
