cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 11)

set(LIB_DIR ${CMAKE_CURRENT_LIST_DIR}/src/main/cpp/lib/)

set(xtide_DIR ${LIB_DIR})
find_package(xtide REQUIRED)

add_library(andxtide SHARED
            src/main/cpp/AndXTideLib.cpp
)

find_library(log-lib
             log
)

target_link_libraries(andxtide
                      xtide
                      ${log-lib}
)