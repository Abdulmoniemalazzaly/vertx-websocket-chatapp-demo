'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var socket = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
    debugger;
    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        socket = new WebSocket('ws://localhost:8888/ws/chatapp/connect');
        socket.onopen = (event) => {
          // Tell your username to the server
          socket.send(
            JSON.stringify({
              sender: username,
              messageType: 'ADD_USER',
              content: null
            })
          )

          connectingElement.classList.add('hidden');
        };

        socket.onmessage = (event) => {
          var message = JSON.parse(event.data);

          var messageElement = document.createElement('li');

          if(message.messageType === 'ADD_USER') {
            messageElement.classList.add('event-message');
            message.content = message.sender + ' joined!';
          } else if (message.messageType === 'DISCONNECT') {
            messageElement.classList.add('event-message');
            message.content = message.sender + ' left!';
          } else {
            messageElement.classList.add('chat-message');

            var avatarElement = document.createElement('i');
            var avatarText = document.createTextNode(message.sender[0]);
            avatarElement.appendChild(avatarText);
            avatarElement.style['background-color'] = getAvatarColor(message.sender);

            messageElement.appendChild(avatarElement);

            var usernameElement = document.createElement('span');
            var usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
          }

          var textElement = document.createElement('p');
          var messageText = document.createTextNode(message.content);
          textElement.appendChild(messageText);

          messageElement.appendChild(textElement);

          messageArea.appendChild(messageElement);
          messageArea.scrollTop = messageArea.scrollHeight;
        }
        socket.onerror = (error) => {
          console.log(error)
          connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
          connectingElement.style.color = 'red';
        }

    }
    event.preventDefault();
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && socket) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            messageType: 'MESSAGE'
        };
        socket.send(JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
