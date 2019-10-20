import React from "react";
import ReactDOM from "react-dom";


import {applyMiddleware, createStore} from 'redux';
import {Provider} from 'react-redux';
import thunk from 'redux-thunk';
import App from './components/App.jsx';

import rootReducer from './reducers';


console.log("hello web");
// use applyMiddleware to add the thunk middleware to the store
const store = createStore(rootReducer, {}, applyMiddleware(thunk));

const wrapper = document.getElementById("my-app");
wrapper ? ReactDOM.render(<Provider store={store}>
    <App/>
</Provider>, wrapper) : false;
