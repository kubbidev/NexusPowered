# NexusPowered
[![license](https://img.shields.io/github/license/kubbidev/NexusPowered?style=for-the-badge&color=b2204c)](LICENSE.txt)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=for-the-badge)](https://github.com/RichardLitt/standard-readme)
[![discord-banner](https://img.shields.io/discord/1238666127073345646?label=discord&style=for-the-badge&color=7289da)](https://discord.kubbidev.com)

NexusPowered is a **Minecraft plugin** utility to reduce boilerplate code in Bukkit plugins.

It is:
* **fast** - written with performance and scalability in mind.
* **free** - available for download and usage at no cost, and permissively licensed so it can remain free forever.

## Table of contents
- [Building](#building)
- [License](#license)

## Building
NexusPowered uses Gradle to handle dependencies & building.

#### Requirements
* Java 21 JDK or newer
* Git

#### Compiling from source
```sh
git clone https://github.com/kubbidev/NexusPowered.git
cd NexusPowered/
./gradlew build
```

You can find the output jars in the `build/libs` directory.

## License
This project is licensed under the [Apache License Version 2.0](LICENSE.txt).