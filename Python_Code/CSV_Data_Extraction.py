import csv
import sys

unique_headers = set()
filename = sys.argv[1] # Pass the file name as an argument

try:
        with open(filename, 'rb') as fin:  
                csvin = csv.reader(fin)
                unique_headers.update(next(csvin, []))
except Exception as e:
        print(e)


print(unique_headers)
