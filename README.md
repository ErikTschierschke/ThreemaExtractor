# ThreemaExtractor

ThreemaExtractor is an extension for the [Mobile Network Analyzer (MoNA)](http://www.bioforscher.de/FoSIL/ippb9076rp8sityx/manager/pages/php/en/software/description/MoNA.php) project.
It provides an Eclipse e4 service that decrypts and parses forensic artifacts from the Threema app for Android devices.

## Dependencies

* [`org.glassfish:javax.json`](https://search.maven.org/artifact/org.glassfish/javax.json/1.1.4/bundle) >= 1.1.4
* [`com.lambdaworks:scrypt`](https://search.maven.org/artifact/com.lambdaworks/scrypt/1.4.0/jar) >= 1.4.0
* [`Willena/sqlite-jdbc-crypt`](https://github.com/Willena/sqlite-jdbc-crypt/releases/tag/3.37.2) >= 3.33.0

## Usage

### Initialization



> **The `de.hsmw.threemaextractor.ui` plugin contains a UI application that can be used to perform this initialization.**



The service entry point is the [`ThreemaExtractor`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/main/ThreemaExtractor.html) class.
To initialize it a [`FileStore`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/main/FileStore.html) object must be built. This class takes the paths of the collected artifacts and checks if they are valid.

```java
FileStore fileStore = new FileStore("[path to key.dat]",
                                    "[path to threema4.db]",
                                    "[path to ch.threema.app_preferences.xml]",
                                    "[path to media directory (data/files/)]",
                                    "[output directoy for decrypted media]");
```

If one of the provided paths doesn’t exist or is not readable a `IOException` is thrown. The paths can be checked with [`FileStore.checkFilePresent(String path)`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/main/FileStore.html#checkFilePresent(java.lang.String)).

#### Passphrases

The master key may be protected by a user-specified passphrase. Run [`FileStore.masterKeyNeedsPassphrase()`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/main/FileStore.html#masterKeyNeedsPassphrase()) to check if a passphrase is required. If the passphrase is known it can be passed to the [`FileStore.setPassphrase(String passphrase)`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/main/FileStore.html#setPassphrase(java.lang.String)) method. The return value is `true` if the passphrase is correct and the master key can be decrypted.



The built `FileStore` can than be passed to the `ThreemaExtractor` class to start the extraction.

```java
ThreemaExtractor threemaExtractor = new ThreemaExtractor(fileStore)
```

### Utilizing the parsed data

The `ThreemaExtractor` object reads all available data and decrypts all media files.

#### User profile information

Data fetched from `ch.threema.app_preferences.xml` can be obtained as [`UserProfile`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/file/UserProfile.html) instance. It contains information from the users profile and a list of blocked contacts.
```java
UserProfile userProfile = threemaExtractor.getUserProfile();
```
#### Communication data

The remaining information is obtained from the `threema4.db` database. It contains contacts, chats, groups, distribution lists and ballots.
This information is represented as [`MainDatabase`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/file/MainDatabase.html) instance.

```
MainDatabase mainDatabase = threemaExtractor.getMainDatabase();
```

This object provides 5 "Store" instances that bundle the corresponding objects.



| Method | Provided Store object | Content |
| -------- | -------- | -------- |
| `mainDatabase.getContacts();`     | [`ContactStore`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/contact/ContactStore.html)     | Threema contacts as [`Contact`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/contact/Contact.html) instances   |
| `mainDatabase.getDirectMessages();`     | [`DirectMessageStore`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/message/DirectMessageStore.html)     | Messages from direct conversations as [`IMessage`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/message/IMessage.html) instances*   |
| `mainDatabase.getGroups();`     | [`GroupStore`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/group/GroupStore.html)     | Groups the user is or was part of as  [`Group`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/group/Group.html) instances   |
| `mainDatabase.getDistributionLists();`     | [`DistributionListStore`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/distribution_list/DistributionListStore.html)     | Distribution lists that the user created as  [`DistributionList`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/distribution_list/DistributionList.html) instances   |
| `mainDatabase.getBallots();`     | [`BallotStore`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/ballot/BallotStore.html)     | Ballots from groups the user is part of as [`Ballot`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/ballot/Ballot.html) instances   |

\* Every message type is represented by a class implementing the `IMessage` interface ([see this overview](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/message/package-summary.html)).

##### Retrieving conversations

Conversations from direct chats can be obtained from the `DirectMessageStore` using the [`getByIdentity(String identity)`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/message/DirectMessageStore.html#getByIdentity(java.lang.String)) where `ìdentity` is the Threema ID of the desired chat partner.
Messages from group conversations can be obtained from [`Group.messages()`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/group/Group.html#messages()).

Both methods return `TreeSet<IMessage>`s. The messages are ordered by the time they were sent.

The [`ChatVisualizer`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/main/ChatVisualizer.html) class may be used to turn these `TreeSet`s into human-readable Strings. 



#### Media files

Files from the media directory are automatically written to the specified output directory.

The files are saved under the following paths.

| Files                           | Path                                                 |
| ------------------------------- | ---------------------------------------------------- |
| media from direct conversations | `media/[chat partner's nickname]/[unique filename*]` |
| media from group conversations  | `group_media/[group name]/[unique filename*]`        |
| user avatar                     | `avatars/user.jpg`                                   |
| contact avatars                 | `avatars/[nickname].jpg`                             |
| group avatars                   | `group_avatars/[group name].jpg`                                                     |

\* Files sometimes have the exact same name (e.g. all voice messages from iOS devices are named `recordAudio.m4a`). Therefore when storing media files 4 digits of the corresponding `MediaMessage`'s UID are prepended.

##### Adressing specific media files

Media may also be adressed individually.

Avatars are represented by the [`Avatar`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/Avatar.html) class. Instances are provided by the folowing methods.

* [`UserProfile.getAvatar()`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/file/UserProfile.html#getUserAvatar())
* [`Contact.contactAvatar()`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/contact/Contact.html#contactAvatar())
* [`Group.groupAvatar()`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/group/Group.html#groupAvatar())

The `Avatar` class provides the `Avatar.writeToFile(File outputDir)` method to save the image to a specific directory.

Media files in conversations are represented by `MediaMessage`s. Those instances provide the [`MediaMessage.saveFile(Masterkey masterkey, File mediaDir, File outDir)`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/message/MediaMessage.html#saveFile(de.hsmw.threemaextractor.service.file.MasterKey,java.io.File,java.io.File)) method to decrypt and save the file to a specific directory.
The unique name of the file can be obtained by the [`MediaMessage.getUniquePlainName()`](https://eriktschierschke.github.io/ThreemaExtractor/javadoc/de/hsmw/threemaextractor/service/data/message/MediaMessage.html#getUniquePlainName()) method.