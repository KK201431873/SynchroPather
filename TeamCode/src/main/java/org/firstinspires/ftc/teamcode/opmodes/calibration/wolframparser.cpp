#include <bits/stdc++.h>
using namespace std;


int main()
{
    return 0;

    ifstream fin ("convention.in");
    ofstream fout ("convention.out");
    string s;
    getline(fin, s);

    //cout << s.length() << endl;

    for (int i = 0; i < s.length(); i++) {
        if (s[i] == '/') {
            fout << ".0/";
        } else if (s[i] == 't' and s[i-1] != '(') {
            if (s[i-1] != '(' and s[i-2] != '+' and s[i-2] != '-') {
                fout << '*';
            }
            fout << "t";
        } else if (s[i] == '$') { // π
            if (s[i-1] != '(' and s[i-2] != '+' and s[i-2] != '-') {
                fout << '*';
            }
            fout << "pi";
        } else if (s[i] == '@') { // θ
            if (s[i-1] != '(' and s[i-2] != '+' and s[i-2] != '-') {
                fout << '*';
            }
            fout << "heavistep";
        } else if (s[i] == 's') {
            if (s[i] == 's' and s[i+1] == 'q' and s[i+2] == 'r' and s[i+3] == 't') {
                if (s[i-1] != '(' and s[i-2] != '+' and s[i-2] != '-') {
                    fout << '*';
                }
                fout <<  "Math.sqrt";
                i += 3;
            } else if (s[i] == 's' and s[i+1] == 'i' and s[i+2] == 'n') {
                if (s[i-1] != '(' and s[i-2] != '+' and s[i-2] != '-') {
                    fout << '*';
                }
                fout <<  "Math.sin";
                i += 2;
            } else {
                fout << s[i];
            }
        } else {
            fout << s[i];
        }
    }
    fout << endl;
    //fout << s << endl;

}