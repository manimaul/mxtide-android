cmake_minimum_required(VERSION 3.4.1)

set(PNG_SRC_DIR ${CMAKE_CURRENT_LIST_DIR}/png/)

add_library(png STATIC
        ${PNG_SRC_DIR}/png.c
        ${PNG_SRC_DIR}/pngerror.c
        ${PNG_SRC_DIR}/pngget.c
        ${PNG_SRC_DIR}/pngmem.c
        ${PNG_SRC_DIR}/pngpread.c
        ${PNG_SRC_DIR}/pngread.c
        ${PNG_SRC_DIR}/pngrio.c
        ${PNG_SRC_DIR}/pngrtran.c
        ${PNG_SRC_DIR}/pngrutil.c
        ${PNG_SRC_DIR}/pngset.c
        ${PNG_SRC_DIR}/pngtrans.c
        ${PNG_SRC_DIR}/pngwio.c
        ${PNG_SRC_DIR}/pngwrite.c
        ${PNG_SRC_DIR}/pngwtran.c
        ${PNG_SRC_DIR}/pngwutil.c
)

target_include_directories(png PUBLIC ${CMAKE_CURRENT_LIST_DIR})
