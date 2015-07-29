import matplotlib.pyplot as plt
from matplotlib.pyplot import *

x = range(5, 65, 5)
ax = plt.subplot(111)
plt.xlim([0, 70]);

y = [1.66, 3.33, 3.75, 4, 4.46, 5, 5, 5.2, 5.425, 5.55, 5.5, 5.2]
p32, = ax.plot(x, y, '-o', color='red')

y = [1.66, 2.5, 3.45, 3.9, 3.77, 4.28, 4.375, 4.44, 4.5, 4.54, 4.58, 4.61]
p24, = ax.plot(x, y, '-o', color='blue')

y = [1.66, 2, 3.2, 3.5, 3.46, 3.75, 3.9, 4, 4.09, 4.16, 4.13, 4]
p16, = ax.plot(x, y, '-o', color='green')

y = [1.66, 1.8, 2.9, 3, 3.47, 3.33, 3.78, 3.63, 3.75, 3.84, 3.66, 3.52]
p8, = ax.plot(x, y, '-o', color='magenta')

y = [1.25, 1.66, 2.54, 2.5, 2.77, 2.72, 2.6, 2.85, 2.8125, 2.63, 2.65, 2.72]
p4, = ax.plot(x, y, '-o', color='cyan')

plt.ylim([0, 8])

box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend([p32, p24, p16, p8, p4], ['32 nodes' , '24 nodes', '16 nodes', '8 nodes', '4 nodes'], loc='center left', bbox_to_anchor=(1, 0.5))

plt.ylabel('Throughput (queries/sec)')
plt.xlabel('Concurrent queries')
savefig('scalability_throughput.png')
plt.show()
