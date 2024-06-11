import React, { useEffect, useRef, useState } from 'react';

const WebSocketComponent = () => {
  const [messages, setMessages] = useState([]);
  const socket = useRef(null);

  useEffect(() => {
    if (socket.current) {
      console.log('WebSocket already connected');
      return;
    }

    // Create a new WebSocket connection
    socket.current = new WebSocket('ws://localhost:3002');

    // Connection opened
    socket.current.addEventListener('open', (event) => {
      console.log('Connected to WebSocket server');
    });

    // Listen for messages
    socket.current.addEventListener('message', (event) => {
      console.log('Message from server:', event.data);
      setMessages((prevMessages) => [...prevMessages, event.data]);
    });

    // Handle connection close
    socket.current.addEventListener('close', (event) => {
      console.log('WebSocket connection closed');
      socket.current = null;
    });

    // Handle connection error
    socket.current.addEventListener('error', (error) => {
      console.error('WebSocket error:', error);
    });

    // Cleanup function
    return () => {
      if (socket.current && socket.current.readyState === WebSocket.OPEN) {
        socket.current.close();
      }
    };
  }, []);

  return null;
};

export default WebSocketComponent;
