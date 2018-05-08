import time
import zmq

context = zmq.Context()
socket = context.socket(zmq.XSUB)
#socket.setsockopt_string(zmq.SUBSCRIBE, 'context')
socket.connect("tcp://localhost:2001")

while True:
 msg = socket.recv() 
 print(msg)
