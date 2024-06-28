# ZUtil

Collection of some free java utilities.

Some of these utilities like [Flags](https://github.com/zoolu-org/zutil/blob/main/src/org/zoolu/util/Flags.java) or [Bytes](https://github.com/zoolu-org/zutil/blob/main/src/org/zoolu/util/Bytes.java) are just single-class implementations that not require any other dependencies, that you can simply copy and include in your projects.
Other tools like [json](https://github.com/zoolu-org/zutil/tree/main/src/org/zoolu/util/json) or [net](https://github.com/zoolu-org/zutil/tree/main/src/org/zoolu/net) are formed by a set of few classes collected within a package. However also in this case there are no dependencies on other exteranl classes.

Here is a brief list of the main included utilities:
* [Flags](https://github.com/zoolu-org/zutil/blob/main/src/org/zoolu/util/Flags.java) - a simple tool for managing command line options;
* [Bytes](https://github.com/zoolu-org/zutil/blob/main/src/org/zoolu/util/Bytes.java) - a collection of useful static methods for managing array of bytes (concatenation, cutting, copy, hex string conversion, integer conversion, etc.);
* [BitString](https://github.com/zoolu-org/zutil/blob/main/src/org/zoolu/util/BitString.java) - invariant string of bits;
* [Parser](https://github.com/zoolu-org/zutil/blob/main/src/org/zoolu/util/Parser.java) - for sequentially parsing string objects (go to, index of, get object, skip, etc.);
* [json](https://github.com/zoolu-org/zutil/tree/main/src/org/zoolu/util/json) - a very small JSON implementation; simple use through the [JsonUtils](https://github.com/zoolu-org/zutil/blob/main/src/org/zoolu/util/json/JsonUtils.java) class;
* [net](https://github.com/zoolu-org/zutil/tree/main/src/org/zoolu/net) - additional classes for handling standard sockets; it includes [UdpProvider](https://github.com/zoolu-org/zutil/blob/main/src/org/zoolu/net/UdpProvider.java), a wrapper of the DatagramSocket that allows asynchronous (event-based) reception of UDP datagrams, and some classes for managing the TLS protocol and digital certificates.

More utilities can be found by browsing the [org/zoolu/util](https://github.com/zoolu-org/zutil/tree/main/src/org/zoolu/util) package.
