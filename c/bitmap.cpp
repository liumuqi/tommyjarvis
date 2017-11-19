#include <iostream>
#include <fstream>
using namespace std;

#define BIT_INT 32 // 1个unsigned int可以标志32个坑
#define SHIFT 5
#define MASK 0x1f
#define N 4294967296 // 2的32次方

unsigned int *a = NULL;

// 必须用堆
void createArr()
{
    a = new unsigned int[1 + N / BIT_INT];
}

void deleteArr()
{
    delete []a;
    a = NULL;
}

// 将所有位都初始化为0状态
void setAllZero()
{
    memset(a, 0, (1 + N / BIT_INT) * sizeof(unsigned int));
}

// 设置第i位为1
void setOne(unsigned int i)
{
    a[i >> SHIFT] |= (1 << (i & MASK));
}

// 设置第i位为1
void setZero(unsigned int i)
{
    a[i >> SHIFT] &= ~(1 << (i & MASK));
}

// 检查第i位的值
int getState(unsigned int i)
{
    return (a[i >> SHIFT] & (1 << (i & MASK))) & 1;
}

// 用bitmap记录是否存在
void setStateFromFile()
{
    ifstream cin("a.txt");
    unsigned int n;
    while(cin >> n)
    {
        setOne(n);
    }
}

// 哈哈
void printCommonNumber()
{
    ifstream cin("b.txt");
    unsigned int n;
    while(cin >> n)
    {
        if(1 == getState(n))
        {
            cout << n << " ";
        }
    }

    cout << endl;
}

int main() 
{
    createArr();
    setAllZero();

    // a.txt: 4 5 7 2 9 2 4 8 0 11 (其实a.txt中可以有40亿个无符号整数)
    // b.txt: 6 11 0 2 3 (其实b.txt中可以有10000个无符号整数)

    setStateFromFile();
    printCommonNumber(); // 11 0 2
    deleteArr();

    return 0;
}
