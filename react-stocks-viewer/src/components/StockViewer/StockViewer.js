import React, { useState, useEffect } from 'react';
import PropTypes from "prop-types";
import "./StockViewer.css";

const StockViewer = () => {
  const [data, setData] = React.useState([]);

  // Similar to componentDidMount and  componentDidUpdate:
  useEffect(() => {
    let eventSource = new EventSource('//localhost:9003/stocks/sse', { withCredentials: true });
    eventSource.onmessage = e => {
      console.log(e);
      if(e == "poisonPill") {
        eventSource.close();
      } else {
        setData( (old) => [...old, JSON.parse(e.data)]);
      }
    }

  });

  return (
    <table className="table table-hover">
    <thead className="thead-dark">
      <tr>
        <th>Time</th>
        <th>Stock</th>
        <th>Price</th>
        <th>Delta</th>
      </tr>
    </thead>
    <tbody>
      {data.map((p) => (
        <tr key={p.timeStamp}>
          <td>{p.latestPrice}</td>
          <td>{p.stock}</td>
          <td>{p.delta}</td>
          <td>{p.timeStamp}</td>
        </tr>
      ))}
    </tbody>
  </table>
  )
}

StockViewer.propTypes = {};

StockViewer.defaultProps = {};

export default StockViewer;
