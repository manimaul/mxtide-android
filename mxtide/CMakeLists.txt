cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 11)

set(xtide_DIR ${CMAKE_CURRENT_LIST_DIR}/src/main/cpp/lib)
find_package(xtide REQUIRED)

add_library(andxtide SHARED
             src/main/cpp/AndXTideLib.cpp
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

#find_library( # Sets the name of the path variable.
#              log-lib
#
#              # Specifies the name of the NDK library that
#              # you want CMake to locate.
#              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       andxtide
                       xtide )
                       # Links the target library to the log library
                       # included in the NDK.
#                       ${log-lib} )