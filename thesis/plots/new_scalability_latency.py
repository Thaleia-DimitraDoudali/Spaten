import matplotlib.pyplot as plt
from matplotlib.pyplot import *

#x = [1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 35, 40]
x = range(5, 65, 5)
ax = plt.subplot(111)
plt.xlim([0, 70]);


y = [2.4, 2.5, 3.2, 3.4, 5, 5.5, 6.37, 6.97, 7.55, 8.34, 8.96, 10.26]
p32, = ax.plot(x, y, '-o', color='red')

y = [2.8, 2.7, 3.06, 3.9, 5.32, 5.76, 6.08, 7.52, 8.31, 9.02, 10.14, 10.88]
p24, = ax.plot(x, y, '-o', color='blue')

y = [2.4, 2.7, 3.6, 4.2, 5.56, 6.36, 7.06, 8.05, 9.02, 10.32, 11.21, 11.3]
p16, = ax.plot(x, y, '-o', color='green')

y = [2.6, 3.2, 3.73, 4, 5.76, 6.03, 7.51, 8.35, 10, 11.02, 11.45, 12.58]
p8, = ax.plot(x, y, '-o', color='magenta')

y = [3, 4.6, 5, 5.95, 6.56, 7.66, 9.91, 10.725, 10.86, 13.6, 13.85, 15.98]
p4, = ax.plot(x, y, '-o', color='cyan')

plt.ylim([0, 17])

box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend([p32, p24, p16, p8, p4], ['32 nodes', '24 nodes', '16 nodes', '8 nodes', '4 nodes'], loc='center left', bbox_to_anchor=(1, 0.5))

plt.ylabel('Latency (sec)')
plt.xlabel('Concurrent queries')
savefig('scalability_latency.png')
plt.show()

