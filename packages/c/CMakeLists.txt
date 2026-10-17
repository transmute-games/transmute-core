cmake_minimum_required(VERSION 3.16)
project(transmute C)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

add_library(transmute
  src/context.c
  src/world.c
  src/body2d.c
  src/gamespec.c
  src/verify.c
  src/playtest.c
)
target_include_directories(transmute PUBLIC include)

enable_testing()
add_executable(test_core tests/test_core.c)
target_link_libraries(test_core PRIVATE transmute)
add_test(NAME test_core COMMAND test_core)

add_executable(hello_headless examples/hello_headless.c)
target_link_libraries(hello_headless PRIVATE transmute)
set_target_properties(hello_headless PROPERTIES
  RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/examples"
)
