# ZooUtil

Collection of some free open source java utilities.

Some of the included tools are:
* [Flags](https://github.com/zooutil/zooutil/blob/main/src/org/zoolu/util/Flags.java) - a simple tool for managing command line options;
* [Bytes](https://github.com/zooutil/zooutil/blob/main/src/org/zoolu/util/Bytes.java) - a collection of useful static methods for managing array of bytes (concatenation, cutting, copy, hex string conversion, integer conversion, etc.);
* [Parser](https://github.com/zooutil/zooutil/blob/main/src/org/zoolu/util/Parser.java) - for sequentially parsing string objects (go to, index of, get object, skip, etc.);
* [BitString](https://github.com/zooutil/zooutil/blob/main/src/org/zoolu/util/BitString.java) - invariant string of bits;
* [json](https://github.com/zooutil/zooutil/tree/main/src/org/zoolu/util/json) - a very small JSON implementation; simple use through the [JsonUtils](https://github.com/zooutil/zooutil/blob/main/src/org/zoolu/util/json/JsonUtils.java) class;
* [net](https://github.com/zooutil/zooutil/tree/main/src/org/zoolu/net) - additional classes for handling standard sockets; it includes [UdpProvider](https://github.com/zooutil/zooutil/blob/main/src/org/zoolu/net/UdpProvider.java), a wrapper of the DatagramSocket that allows asynchronous (event-based) reception of UDP datagrams, and some classes for managing the TLS protocol and digital certificates.