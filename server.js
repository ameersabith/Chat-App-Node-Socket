/**
*Created by Ameer Sabith
**/

const express = require('express'),
  http = require('http'),
  app = express(),
  server = http.createServer(app),
  socketIo = require('socket.io');

  // socketIo = require('socket.io').listen(server);
var io = socketIo.listen(server);

app.get('/', (req, res) => {
  res.send('Chat Server is running on port 3000')
});

io.on('connection', (socket) => {

  console.log(`user connected : Socket ID = ${socket.id}`)

  var userName = '';

  socket.on('join', (data) => {
    console.log('Join triggered.');
    const roomData = JSON.parse(data);
    userName = roomData.userName;
    const roomName = roomData.roomId;

    socket.join(`${roomName}`)
    console.log(userName +" : has joined the chat in " + roomName);

    io.to(`${roomName}`).emit('newUserJoinedTheChat', userName)

  });

  socket.on('newMessage', (data) => {
    console.log('newMessage triggered')

    //log the message in console 
    const messageData = JSON.parse(data)
    const messageContent = messageData.messageContent
    const roomName = messageData.roomId

    console.log(`[Room Number ${roomName}] ${userName} : ${messageContent}`)
        // Just pass the data that has been passed from the writer socket
   
    //create a message object 
    /*let  message = {"message":messageContent, 
                      "userName":userName}*/

    const messageObject = {
      name : userName,
      messageContent : messageContent,
      roomId : roomName
    }
        
    // send the message to all users including the sender  using io.emit  
    /*socketio.emit(
      'message',
       message 
    )*/
    socket.broadcast.to(`${roomName}`).emit('message', JSON.stringify(messageObject))
  });

  // socket.on('typing',function(roomNumber){ //Only roomNumber is needed here
    //     console.log('typing triggered')
    //     socket.broadcast.to(`${roomNumber}`).emit('typing')
    // })

    // socket.on('stopTyping',function(roomNumber){ //Only roomNumber is needed here
    //     console.log('stopTyping triggered')
    //     socket.broadcast.to(`${roomNumber}`).emit('stopTyping')
    // })

  socket.on('unsubscribe',function(data) {
        console.log('unsubscribe trigged')
        const roomData = JSON.parse(data)
        const userName = roomData.userName;
        const roomName = roomData.roomId;
    
        console.log(`Username : ${userName} leaved Room Name : ${roomName}`)
        socket.broadcast.to(`${roomName}`).emit('userLeftChatRoom',userName)
        socket.leave(`${roomName}`)
    })

  socket.on('disconnect', () => {
    console.log(userName +' has left ')
    socket.broadcast.emit( "userdisconnect" ,' user has left')   
  });
});

server.listen(3000,()=>{
  console.log('Node app is running on port 3000')
});