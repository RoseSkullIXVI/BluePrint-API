import csv
import sys

unique_headers = set()
try:
        with open(filename, 'rb') as fin:  
                csvin = csv.reader(fin)
                unique_headers.update(next(csvin, []))
except Exception as e:
        print(e)


print(unique_headers)
