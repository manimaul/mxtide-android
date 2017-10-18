cmake_minimum_required(VERSION 3.4.1)

set(TCD_SRC_DIR ${CMAKE_CURRENT_LIST_DIR}/tcd/)

add_library(tcd STATIC
    ${TCD_SRC_DIR}/tide_db.c
    ${TCD_SRC_DIR}/bit_pack.c
)
target_include_directories(tcd PUBLIC ${CMAKE_CURRENT_LIST_DIR})