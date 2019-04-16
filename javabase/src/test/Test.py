#!/usr/local/bin/python
class Solution:
    def shortestPalindrome(self, s):
        """
        :type s: str
        :rtype: str
        """
        ss = s[::-1]
        n = len(ss)
        for i in range(len(s), -1, -1):
            if ss[n - i:] == s[:i]:
                break
        return (ss[:n - i] + s)
