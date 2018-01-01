cmake_minimum_required(VERSION 3.4.1)

PROJECT(jmxtide)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../mxtide-core ${CMAKE_CURRENT_BINARY_DIR}/../mxtide-core)

set(SOURCE_SET
    src/main/cpp/Jni.cpp
    src/main/cpp/Jni.h
    src/main/cpp/JniArrayList.cpp
    src/main/cpp/JniArrayList.h
    src/main/cpp/JniFloat.cpp
    src/main/cpp/JniFloat.h
    src/main/cpp/JniStation.cpp
    src/main/cpp/JniStation.h
    src/main/cpp/JniStationPrediction.cpp
    src/main/cpp/JniStationPrediction.h
    src/main/cpp/JniStationType.h
    src/main/cpp/JniString.cpp
    src/main/cpp/JniString.h
    src/main/cpp/JniTidesAndCurrents.cpp
    src/main/cpp/JniTidesAndCurrents.h
)

add_library(jmxtide SHARED ${SOURCE_SET})
target_link_libraries(jmxtide mxtide)
