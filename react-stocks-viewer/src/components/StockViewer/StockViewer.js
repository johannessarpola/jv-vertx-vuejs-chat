import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import "./StockViewer.css";
import 'bootstrap/dist/css/bootstrap.min.css';
import moment from "moment";


const StockViewer = props => {
  const [data, setData] = React.useState([]);

  // Similar to componentDidMount and  componentDidUpdate:
  useEffect(() => {

    const poison = e => {
      console.log("received poison, closing");
      eventSource.close();
    };
  
    const onmessage = e => {
      //console.log(e);
      const obj = JSON.parse(e.data);
      console.log(obj);
      
      setData(old => {
        obj.index = old.length;
        return [...old, obj]; 
      });
    };

    let eventSource = new EventSource("//localhost:9003/stocks/sse", {
      withCredentials: true
    });

    eventSource.addEventListener("poison", poison)
    eventSource.onmessage = onmessage;

  }, []); // [] to run this only once

  return (
    <table className="table table-hover">
      <thead className="thead-dark">
        <tr>
          <th>Nr</th>
          <th>Stock</th>
          <th>Price</th>
          <th>Delta</th>
          <th>Time</th>
        </tr>
      </thead>
      <tbody>
        {data.map(p => (
          <tr key={p.index}>
            <td>{p.index}</td>
            <td>{p.stock}</td>
            <td>{Number(p.latestPrice).toFixed(2)}</td>
            <td>{Number(p.delta).toFixed(2)}</td>
            <td>{moment(p.timeStamp).format("YYYY/MM/DD HH:mm:ss")}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

StockViewer.propTypes = {};

StockViewer.defaultProps = {};

export default StockViewer;
