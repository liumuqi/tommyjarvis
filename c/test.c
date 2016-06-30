#include <stdio.h>
#include "hello.h"
#include <string.h>
#include <sys/msg.h>
#include <stdint.h>
#include <stdlib.h>

#define TEST_DE 2000



#include <stdint.h>

//  Windows
#ifdef _WIN32

#include <intrin.h>
uint64_t rdtsc(){
    return __rdtsc();
}

//  Linux/GCC
#else

uint64_t rdtsc(){
    unsigned int lo,hi;
    __asm__ __volatile__ ("rdtsc" : "=a" (lo), "=d" (hi));
    return ((uint64_t)hi << 32) | lo;
}
#endif

uint64_t GetCpuCycles()
{
    uint32_t lo,hi;
    __asm__ __volatile__ ( "rdtsc":"=a"(lo),"=d"(hi));
    return (uint64_t)hi<<32|lo;
}
int get_random_seed()
{
    uint64_t cpu_cycles = GetCpuCycles();
    uint32_t r_seed = cpu_cycles & 0xffffffff;
    srand(r_seed);
    return rand_r(&r_seed);
}
int main(int argc, char const* argv[])
{
    puts("test>>>>>>>>>>>>>>>>>>haha");
    printf("%03d\n",7777 );
    char *str = "100cc200";
    int a;
    sscanf(str,"%d",&a);
    printf(">>>>>>>>>>>>>>%d\n",a );

    char *result;
    printf("%d\n", TEST_DE);


    int x = get_random_seed();
    printf("======================================%d\n",x);
    return 0;
}
