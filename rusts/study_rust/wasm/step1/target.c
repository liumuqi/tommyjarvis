#include <stdio.h>
#include <emscripten.h>

int main()
{
    printf("in wasm world\n");
    EM_ASM({ alert('Hello World!');  });
    return 1;
}
