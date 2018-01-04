cmake_minimum_required(VERSION 3.4.1)

set(MX_TIDE_CORE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../mxtide-core)
set(LIB_DIR ${MX_TIDE_CORE_DIR}/src/main/cpp/lib/)

set(xtide_DIR ${LIB_DIR})
find_package(xtide REQUIRED)

include_directories(src/main/cpp/include)

file(GLOB SOURCE_SET
        "${MX_TIDE_CORE_DIR}/src/main/cpp/*.cpp"
        "${MX_TIDE_CORE_DIR}/src/main/cpp/*.h"
        "${MX_TIDE_CORE_DIR}/src/main/cpp/include/*.h"
        "src/main/cpp/*.cpp"
        "src/main/cpp/*.h"
        )

list(REMOVE_ITEM SOURCE_SET ${MX_TIDE_CORE_DIR}/src/main/cpp/main.cpp)
list(REMOVE_ITEM SOURCE_SET ${MX_TIDE_CORE_DIR}/src/main/cpp/PerfTimer.h)

set(LINK_LIBS mxtide xtide)

add_library(mxtide SHARED ${SOURCE_SET})
target_include_directories(mxtide PUBLIC ${MX_TIDE_CORE_DIR}/src/main/cpp/include)

target_link_libraries(${LINK_LIBS})
