---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Hi:Re Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------
<div style="page-break-after: always; visibility: hidden"> \pagebreak </div>

## **Acknowledgements**

Hi:Re is a brownfield project that builds on the Addressbook Level-3 project. Both the product and inspiration are projects
under the module CS2103T: Software Engineering from the National University of Singapore, offered by the School of Computing.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `+ /name Tan Ah Gao /id chihuahua69 /hp 999 /tag finance`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("- id abc123")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is first passed to an `AccountManagerParser` object which in turn creates a parser that matches the command (e.g., `LoginCommandParser`) and uses it to parse the command.
1. If the parsing result returned by `AccountManagerParser` is null, the command is then passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AccountManagerParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `LoginCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `LoginCommand`) which the `AccountManagerParser` returns back as a `Command` object.
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the tag data i.e., all `Tag` objects (which are contained in a `TagList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div style="page-break-after: always; visibility: hidden"> \pagebreak </div>

### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data, user preference data and tag list data in JSON format, and read them back into corresponding objects.
* inherits from `AddressBookStorage`, `UserPrefStorage` and `TagListStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------
<div style="page-break-after: always; visibility: hidden"> \pagebreak </div>


## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Implemented\] Edit feature

#### Current Implementation

The edit mechanism was previously based on the index of the contact in the shown list of contacts,
but is now fitted to serve our new key identifier of an employee, namely the ID.
In addition, the ID of an employee now cannot be edited by any means (intuitive logic of unique ID),
hence any ID changes would mean that the employee needs to be deleted and added again with their new ID and information.

<puml src="diagrams/EditCommandFlow.puml" alt="EditCommandFlow" />

#### Kept changes from previous implementation
- Optional to edit all fields, at least 1 field is enough
- Fields can be entered in any order

#### Pros of ID as key
- Ensure that random employee details are not edited by accident
- Every edit command is now more purposeful as if ID is not found, edit does not go through

#### Cons of ID as key
- Slightly slower to type in than index
- Slightly slower to execute if employee list shown is huge



### \[Implemented\] Undo/redo feature

Our undo/redo mechanism is facilitated by a `CommandList` inside the `Model` and an internal pointer to track the current command, offering a command-centric approach, which can be highly efficient and flexible.<br><br>

**Core Components**
1. `CommandList`: This list stores all the commands that have been executed. Each command in this list should be capable of reversing its effect (undo) and reapplying its effect (redo).

2. `CommandList.currentCommandIndex`: This pointer indicates the current position in the commandList. It helps determine which commands have been executed and which ones have been undone.

**Functionalities**
1. Executing a command:
   * When a new command is executed, it is added to the end of the commandList.
   * The current command pointer is moved to this new command's position.
   * Commands following the current command pointer (if any) are cleared from the list, as the new command creates a new "branch" of history, invalidating the redo history.<br><br>

2. Undoing a command:
   * To undo, check if the pointer is not at the beginning of the list.
   * The command at the current pointer position knows how to undo itself. It executes its undo method.
   * The pointer is then decremented, moving backwards in the command list.<br><br>

3. Redoing a command:
   * To redo, check if the pointer is not at the end of the list.
   * Increment the pointer to move to the next command in the list.
   * The command at this new pointer position knows how to redo itself and executes its redo method.<br><br>

4. Self-contained commands:
   * Each command object contains all the information necessary to both apply and reverse its effects. When a command is executed, it also prepares for its potential undo by storing any relevant state changes or backups of the data it modifies. Currently our undo/redo only supports the following commands.
   * Add: Stores a reference to the added person. To undo, it removes this person from the address book. To redo, it simply re-adds the stored person to the address book.
   * Delete: Stores a reference to the deleted person. To undo, it re-adds this person to the address book using the stored details. To redo, is removes the person again from the address book.
   * Edit: Stores a reference to the person before edit and another reference to the person after edit. To undo, it removes the person after edit and re-adds the person before edit. To redo, it removes the person before edit and re-adds the person after edit.
   * Clear: Stores a reference to the address book before clearing. To undo, it restores the address book to the state before clearing. To redo, it clears the address book again.


#### Design considerations:

Compared with the recommended way that stores the entire address book for each undo/redo, our command-centric approach has the following pros and cons:

**Pros:**

* Reduced Memory Usage: Since only changes are stored rather than the entire application state, this method typically consumes less memory. For instance, the delete command only needs to store the details of the deleted person, not the state of the entire address book.

* Scalability: As operations increase in complexity or frequency, storing individual command effects rather than snapshots of the entire state scales better. This makes the system more efficient and responsive as the volume of data grows.

* Flexibility and Modularity: Commands encapsulate their functionality, making the system more modular. This separation allows for easier updates and modifications to individual commands without affecting the overall system.

* Targeted Undo/Redo: This approach allows for precise control over what gets undone or redone, making the operations more predictable and less prone to errors that can occur when rolling back a full state.

**Cons:**

* Implementation Complexity: Each command must be correctly implemented with its own undo and redo logic. This increases the complexity of each command's implementation and requires careful design to ensure that each command properly and completely reverses its effects.

* Testing Overhead: The need for thorough testing increases as each command has its own undo/redo logic. There's a higher risk of bugs in individual commands, especially in scenarios where commands might interact or affect the results of each other.

* Dependency and State Consistency: Commands must be designed not to rely excessively on the specific state of the application that may be altered by other commands. Ensuring consistency and managing dependencies between commands can be challenging.

* Recovery from Errors: If an error occurs during the execution of an undo or redo operation, recovering and ensuring data integrity can be more complex, as the system must handle partial undos or redos gracefully.
### \[Implemented\] Data archiving

Currently data is being archived via storing it locally as a `.json` file. Our application supports exporting data to a CSV file, offering a convenient way to handle data outside the application. <br><br>

**Core Components**
1. `ExportCommand`: This class handles the logic for the export operation. It checks if the file exists to prevent overwriting and throws an exception if the file already exists. It also handles the creation of the CSV file by delegating to the storage class.

2. `JsonCsvAddressBookStorage`: This class extends the storage capabilities to support CSV format. It handles reading from and saving to JSON and CSV formats. It uses utility functions to convert JSON data directly into CSV format.

3. `JsonUtil`: This utility class provides methods to read from and write to JSON files, and includes functionality to convert JSON data to CSV format.

**Functionalities**
1. Executing the Command:
   * The command checks if the target file already exists to avoid accidental overwriting. If the file exists, it throws an exception.
   * If the file does not exist, it initializes a new JsonCsvAddressBookStorage with the target file path.
   * It then calls the save method to write the address book data to a CSV file at the specified location.<br><br>

2. Handling file operations:
   * The utility functions in JsonUtil handle the conversion of data formats and manage file I/O operations, ensuring data integrity during the read/write process.
   
**Design considerations:**

**Pros:**

* User Convenience: Allows users to export their data easily, making data portable and usable in other applications like spreadsheets.
* Data Integrity: By checking for file existence before performing operations, the feature prevents data loss from accidental overwrites.
* Extensibility: The use of a dedicated storage class for handling CSV operations keeps the command class focused on command logic, promoting cleaner code separation and making it easier to extend or modify file handling operations in the future.

**Cons:**

* Error Handling Complexity: Managing file-related errors (e.g., file access permissions, disk space issues) requires robust exception handling that might complicate the command execution flow.
* Dependency on File System: As operations depend on file I/O, the feature's reliability is contingent upon the underlying file system, which can introduce issues not directly related to the application's logic (e.g., file system availability, network issues for network-mounted storage).
* Testing Overhead: The need to mock file I/O operations in unit tests increases the complexity of testing this feature, requiring comprehensive mocks and potentially complex setup procedures for tests.


--------------------------------------------------------------------------------------------------------------------
<div style="page-break-after: always; visibility: hidden"> \pagebreak </div>


## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* HR / Executives with access to company contactsw
* needs to update / contact people frequently
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: access and manage multitudes of contacts quickly and easily

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​             | I want to …​                                 | So that I can…​                                                         |
|----------|--------------------|---------------------------------------------|------------------------------------------------------------------------|
| `* * *`  | HR employee        | add new contacts                            |                                                                        |
| `* * *`  | HR employee        | delete contacts                             | remove entries that I no longer need                                   |
| `* * *`  | HR employee        | toggle address book visibility              | be able to hide it for simplicity                                      |
| `* *`    | first time user    | not have a cluttered GUI                    | will not be confused                                                   |
| `* *`    | potential user     | see examples in the database                | examine how it should look like when I start using the app for work    |
| `* *`    | frequent user      | optimize command typing                     | accomplish work efficiently                                            |
| `* *`    | frequent user      | quickly scan the database                   | check for missing details                                              |
| `* *`    | frequent user      | search for specific information             | optimize my work performance                                           |
| `* *`    | potential user     | update contact details                      | keep the information in the book up to date                            |
| `* *`    | HR manager         | search for contacts via their attributes    | can easily find a specific person                                      |
| `* *`    | frequent user      | automatically delete duplicate contacts     | stay organized                                                         |
| `* *`    | HR manager         | sort employee contacts via their attributes | can find employees that fit specific criteria                          |
| `* *`    | employer           | be informed of understaffed departments     | HR can hire accordingly                                                |
| `* *`    | HR manager         | attach tags or notes to contacts            | adding personal reminders or categorizations                           |
<!--
| `* * *`  | new user                                   | see usage instructions       | refer to instructions when I forget how to use the App                 |
| `* * *`  | user                                       | add a new person             |                                                                        |
| `* * *`  | user                                       | delete a person              | remove entries that I no longer need                                   |
| `* * *`  | user                                       | find a person by name        | locate details of persons without having to go through the entire list |
| `* *`    | user                                       | hide private contact details | minimize chance of someone else seeing them by accident                |
| `*`      | user with many persons in the address book | sort persons by name         | locate a person easily                                                 |

*{More to be added}*
-->

### Use cases

(For all use cases below, the **System** is the `Hi:Re` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC1 - Add a contact**

**MSS**

1. User requests to add a new contact and input the contact information
1. Hi:Re adds the person to the database
1. Hi:Re shows a message for the successful addition

   Use case ends.

**Extensions**

* 1a. The input format is wrong and cannot be accepted

    * 1a1. Hi:Re shows an error message

      Use case ends.


**Use case: UC2 - Delete a contact**

**MSS**

1. User requests to delete a specific person in the database and input the details
1. Hi:Re deletes the person
1. Hi:Re shows a message for the successful deletion

    Use case ends.

**Extensions**

* 1a. The given details are invalid.

    * 1a1. Hi:Re shows an error message

      Use case ends.

* 2a. The given person does not exist.

    * 2a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC3 - Edit a contact**

**MSS**

1. User requests to edit a specific person in the database and input the details
1. Hi:Re edits the selected fields of the person
1. Hi:Re shows a message for the successful edit

    Use case ends.

**Extensions**

* 1a. The given details are invalid.

    * 1a1. Hi:Re shows an error message

      Use case ends.
  
* 2a. The given person does not exist.

    * 2a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC4 - Toggle display**

**MSS**

1. User requests to toggle the display on or off
2. Hi:Re hides or shows the display, based on whether the display was formerly on or off

   Use case ends.

**Extensions**

* (No extensions)

**Use case: UC5 - Clear all contacts**

**MSS**

1. User requests to clear all contacts in the database
1. Hi:Re clears the database
1. Hi:Re shows a message for the successful clear

    Use case ends.

**Extensions**

* (No extensions)

**Use case: UC6 - Find contacts by name**

**MSS**

1. User requests to find contacts with specific keywords in their name
1. Hi:Re finds matching contacts
1. Hi:Re shows a message for the successful search

    Use case ends.

**Extensions**

* (No extensions)

**Use case: UC7 - List all contacts**

**MSS**

1. User requests Hi:Re to list all contacts
1. Hi:Re lists all contacts
1. Hi:Re shows a message for the successful listing

    Use case ends.

**Extensions**

* (No extensions)

**Use case: UC8 - List all tags**

**MSS**

1. User requests Hi:Re to list all tags
1. Hi:Re lists all tags
1. Hi:Re shows a message for the successful listing

    Use case ends.

**Extensions**

* (No extensions)

**Use case: UC9 - List all contacts with specific tags**

**MSS**

1. User requests Hi:Re to list all contacts with specific tags
1. Hi:Re lists all matching contacts
1. Hi:Re shows a message for the successful listing

    Use case ends.

**Extensions**

* (No extensions)

**Use case: UC10 - Add tags**

**MSS**

1. User requests Hi:Re to add a tag to the database
1. Hi:Re adds the tag
1. Hi:Re shows a message for the successful addition

    Use case ends.

**Extensions**

* 1a. The given tag name is invalid.

    * 1a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC11 - Delete tags**

**MSS**

1. User requests Hi:Re to remove a tag from the database
1. Hi:Re removes the tag
1. Hi:Re shows a message for the successful deletion

    Use case ends.

**Extensions**

* 1a. The given tag name is invalid.

    * 1a1. Hi:Re shows an error message

      Use case ends.

* 2a. The given tag name is not in the database.

    * 2a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC12 - Undo command**

**MSS**

1. User requests Hi:Re to undo previous commands
1. Hi:Re undoes last command
1. Hi:Re shows a message for the successful undo

    Use case ends.

**Extensions**

* 1a. There are no commands to undo.

    * 1a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC13 - Redo command**

**MSS**

1. User requests Hi:Re to redo previous undone command
1. Hi:Re redoes last undone command
1. Hi:Re shows a message for the successful redo

    Use case ends.

**Extensions**

* 1a. There are no commands to redo.

    * 1a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC14 - Export data**

**MSS**

1. User requests Hi:Re to export data from database
1. Hi:Re exports data to CSV file in the same directory as the application.
1. Hi:Re shows a message for the successful export

    Use case ends.

**Extensions**

* 1a. Filename given is invalid.

    * 1a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC15 - Open guide**

**MSS**

1. User requests Hi:Re for the user guide
1. Hi:Re opens a window with a copyable link to the user guide
1. Hi:Re shows a message for the successful opening of the help window

    Use case ends.

**Use case: UC16 - Register user**

**MSS**

1. User attempts to register to Hi:Re 
1. Hi:Re saves user's username and encrypted password 
1. Hi:Re shows a message for the successful registration

    Use case ends.

**Extensions**

* 1a. Username given is invalid.

    * 1a1. Hi:Re shows an error message

      Use case ends.

* 2a. Password given is invalid.

    * 2a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC17 - Log into System**

**MSS**

1. User attempts to log into Hi:Re with a given username and password.
1. Hi:Re logs the user in
1. Hi:Re shows a message for the successful login

    Use case ends.

**Extensions**

* 1a. The given details are invalid.

    * 1a1. Hi:Re shows an error message

      Use case ends.

**Use case: UC18 - Log out of System**

**MSS**

1. User attempts to log out of Hi:Re.
1. Hi:Re logs the user out
1. Hi:Re shows a message for the successful logout

    Use case ends.

**Extensions**

* 1a. User is not logged into the system (and thus cannot be logged out)

    * 1a1. Hi:Re shows an error message

      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 employees without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should take less than 3 seconds for the application to finish processing a command and display the results.
5.  Should work on any 32 bit or 64 bit OS.
6.  Should be usable by any regular employee with little to no technical expertise.
7.  Should have no differences in behaviour or performance between Windows 10 and 11.
8.  A HR employee should not be able to see sensitive information but an executive or someone with higher system authority can.

### Glossary

* **Mainstream OS**: Operating Systems (OS) such as Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **HR**: Human Resource, a department associated with managing employee benefits and recruitment
* **MSS**: Main Success Scenario, the main flow of events in a use case
* **Technical expertise**: Any relevant experience in using a computer and software on it
* **Sensitive information**: Important information relating to a person that may cause privacy concerns such as ID details or bank account details.

--------------------------------------------------------------------------------------------------------------------
<div style="page-break-after: always; visibility: hidden"> \pagebreak </div>

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.


### Adding a contact

For manual testing of other commands, run the following commands to add the specified few contacts
(this is assuming the default tag list given is not edited in any way):
  1) `+ /name John Doe /id johndoe61 /hp 98765432 /tag finance`
  2) `+ /name Ali /id ali21 /hp 99795422 /tag sales`
  3) `+ /name Rose /id rose84 /hp 98631422 /tag RnD`

1. Adding a person while display is off.

   1. Prerequisites:
     * Clear all contacts using the `clear` command.
     * Toggle the display using the `$` command, to turn it off.
     * Ensure the tag `RnD` exists in the tag list by running `ls -t`
   
   1. Test case: `+ /name Rose /id rose84 /hp 98631422 /tag RnD` <br>
      Expected: Contact is added. Details of added contact shown in message box. Display remains off.
   2. Test case: `+ /name Rose /id rose84 /tag RnD` <br>
      Expected: No contact is added. Error details shown in the message box. Display remains off.

1. Adding a person with S/O or hyphen in the name.

    1. Prerequisites:
    * Clear all contacts using the `clear` command.
    * Ensure the tag `RnD` exists in the tag list by running `ls -t`

    1. Test case: `+ /name Rose S/O Dan /id rose84 /hp 98631422 /tag RnD` <br>
       Expected: No contact is added. Error details shown in the message box.
    2. Test case: `+ /name Rose-Dan /id rose84 /hp 98631422 /tag RnD` <br>
       Expected: No contact is added. Error details shown in the message box.

### Editing a contact

1. Editing a person with a tag not in the tag list.

    1. Prerequisites:
    * Clear all contacts using the `clear` command.
    * Add a few contacts in according to [this section](#adding-a-contact).
    * The following is done with the default tag list.

    1. Test case: `> rose84 /tag ABCDEFG` <br>
       Expected: Contact is not edited. Error details shown in the message box.
    2. Test case: `> rose84 /tag Finance` <br>
       Expected: Contact is not edited. Error details shown in the message box. Tags are **case-sensitive**.    

### Deleting a contact

1. Deleting a person while display is off.

   1. Prerequisites: 
      * Clear all contacts using the `clear` command. 
      * Add a few contacts in according to [this section](#adding-a-contact).
      * Toggle the display using the `$` command, to turn it off.

   1. Test case: `- /id johndoe61`<br>
      Expected: Contact with the ID johndoe61 will be deleted. Details of the deleted contact shown in the message box. Display remains off. 

   1. Test case: `- /id `<br>
      Expected: No person is deleted. Error details shown in the message box.

### Clearing the addressbook

1. Clearing the addressbook while display is off.

   1. Prerequisites: 
   * Clear all contacts using the `clear` command.
   * Add a few contacts in according to [this section](#adding-a-contact).
   * Toggle the display using the `$` command, to turn it off.
   <br><br>

   1. Test case: `clear`<br>
      Expected: Addressbook will be cleared. Success message will be displayed. Display remains off. 

   1. Test case: `clear this` _(where there are additional inputs after `clear`)_<br>
      Expected: Similar to previous.

### Adding tags

1. Invalid commands / inputs

   1. Test case: Non-alphanumeric: `tag+ Human Resource`, `tag+ $@Les`<br>
   Expected: No tags added. Error message will be displayed.
   
   2. "Multiple" tags: `tag+ HR, Finance`, `tag+ sales + marketing`<br>
   Expected: Similar to previous.


2. Adding duplicate inputs

   1. Prerequisites:
   * Add in a new tag, e.g. `tag+ test`
   <br><br>
   
   2. Test case: `tag+ test` _(2nd time)_<br>
   Expected: No tags added. Error message will be displayed.

   3. Test case: `tag+ Test` _("duplicated" tag)_<br>
   Expected: Tag will be added. Success message will be displayed.<br>
   Tags are **case-sensitive**.

### Deleting tags

1. Removing a tag that is in use.

   1. Prerequisites: 
   * Clear all contacts using the `clear` command.
   * Add a few contacts in according to [this section](#adding-a-contact).
   <br><br>
   
   2. Test case: `tag- finance`<br>
   Expected: No tag removed. Error message will be displayed.<br>
   Tag is still in use by a contact in the addressbook.

### Finding contacts

1. Entering a keyword that does not match any contact entries (case-sensitive)

    1. Prerequisites:
    * Clear all contacts using the `clear` command.
    * Add a few contacts in according to [this section](#adding-a-contact).

    1. Test case: `? kevin` <br>
       Expected: No matching contacts found. Message displayed indicates as such.
    2. Test case: `? jOhN` <br>
       Expected: One matching contact found (John). Keywords are **case-insensitive**.

### Listing contacts

1. Finding a person with a tag which does not match those in the tag list. (case-sensitive)

    1. Prerequisites:
    * Clear all contacts using the `clear` command.
    * Add a few contacts in according to [this section](#adding-a-contact).
    * The following is done with the default tag list.

    1. Test case: `ls ABCDEFG` <br>
       Expected: No contacts listed. Message displayed indicates as such.
    2. Test case: `ls Finance` <br>
       Expected: 1 contact listed. Message displayed indicates as such. Tags are **case-insensitive**.

### Register an account

1. Invalid inputs.
   
   1. Invalid username: `register /u 12 /p abc12345`<br>
      Expected: No account created. Error message will be displayed.

   2. Invalid password: `register /u test /p 123`<br>
      Expected: No account created. Error message will be displayed.

2. Invalid command.
   
   1. Missing field: `register /u test`<br>
      Expected: No account created. Error message will be displayed.<br><br>

3. Duplicate username.
   
   Test case:<br><br>
   Execute `register /u test /p abc12345` twice.<br><br>
   Expected: Only the first command will create an account. Error message will be displayed for the second command.

### Login an account

1. Invalid inputs.

    1. Invalid username: `login /u 12 /p abc12345`<br>
       Expected: Login fails. Error message will be displayed.

    2. Invalid password: `login /u test /p 123`<br>
       Expected: Login fails. Error message will be displayed.

2. Invalid command.

    1. Missing field: `login /u test`<br>
       Expected: Login fails. Error message will be displayed.<br><br>

3. Another user already logged in.

   Test case: 
   1. Execute `register /u test /p abc12345`.<br>
   2. Execute `login /u test /p abc12345` twice.<br><br>
   
   Expected: Only the first login command will succeed. Error message will be displayed for the second login command.

### Logout an account

1. The user has not logged in.

   Test case: <br><br>
   Launch Hi:Re and then execute `logout` first.<br><br>
   Expected: Logout fails. Error message will be displayed.

### Undo and Redo

1. Undoing and redoing an add command

   1. Prerequisites: List all contacts using the `ls -a` command. Then, add a contact using the `+` command.

   1. Test case: `undo` <br>
      Expected: Added contact disappears from the list. Message indicating successful undo is printed. Subsequent `ls -a` should NOT show this contact.  
      
   1. Test case: `undo`, followed by `redo`. <br>
      Upon `redo`, added contact reappears in the list. Subsequent `ls -a` should show this contact. Message indicating successful redo is printed.

1. Undoing and redoing a delete command

   1. Prerequisites: List all contacts using the `ls -a` command. Then, delete a contact using the `-` command.

   1. Test case: `undo` <br>
      Expected: Upon `undo`, deleted contact is reappears in the list. Subsequent `ls -a` should show this contact. Message indicating successful undo is printed.

   1. Test case: `undo`, followed by `redo` <br>
      Expected: Upon `redo`, deleted contact disappears from the list. Subsequent `ls -a` should NOT show this contact. Message indicating successful redo is printed.

1. Undoing and redoing an edit command

   1. Prerequisites: List all contacts using the `ls -a` command. Then, edit a contact field using the `>` command.

   1. Test case: `undo` <br>
      Expected: Upon `undo`, specified field of edited contact reverts. Subsequent `ls -a` should show this reverted contact. Message indicating successful undo is printed.

   1. Test case: `undo`, followed by `redo` <br>
      Expected: Upon `redo`, contact retains edits performed in the preqreuisite stage. Subsequent `ls -a` should show this edited contact. Message indicating successful redo is printed.

1. Undoing and redoing a clear command

   1. Prerequisites: List all contacts using the `ls -a` command. Then, clear all contacts using the `clear` command. 

   1. Test case: `undo` <br>
      Expected: Upon `undo`, all cleared contacts reappear in the list. Subsequent `ls -a` should show all contacts upon initial prerequisite `ls -a`. Message indicating successful undo is printed.

   1. Test case: `undo`, followed by `redo` <br>
      Expected: Upon `redo`, no contacts should be listed. Subsequent `ls -a` should continue to show an empty list. Message indicating successful redo is printed.

1. No commands to undo / redo
   1. Preqreuisites: Have had just started the application and logged in without executing any other commands
   
   1. Test case: `undo`
      Expected: No change to the list of contacts observed. Message printed indicating that there are no commands to undo.

   1. Test case: `redo`
      Expected: No change to the list of contacts observed. Message printed indicating that there are no commands to redo.

1. Chaining `undo`s and `redo`s

   1. Prerequisites: List all contacts using the `ls -a` command. Then, perform three `+` commands with different `/id` fields, e.g. <br>
      1. `/name A /id 1 /hp (handphone) /tag (tag)`, followed by
      1. `/name B /id 2 /hp (handphone) /tag (tag)`, followed by
      1. `/name C /id 3 /hp (handphone) /tag (tag)`

   1. Test case: Perform `undo` three times.
      Expected: Upon first undo, third added contact (`C` in the example) is deleted. Upon second undo, second added contact (`B` in the example) is deleted. Upon third undo, first added contact (`A` in the example) is deleted. 

   1. Test case: Perform `undo` three times. Then, perform `redo` three times.
      Expected: Upon first redo, only first added contact (`A` in the example) is added back. Upon second redo, second added contact (`B` in the example) is added back. Upon third redo, third added contact (`C` in the example) is added back. 

1. Non-`undo`-able commands

   1. Prerequisites: List all contacts using the `ls -a` command. Then, add a contact using the `+` command.

   1. Test case: Perform a `?` command, followed by an `undo`.
      Expected: Added contact disappears from the list. Message indicating successful undo is printed. Subsequent `ls -a` should NOT show this contact. 
   
   1. Test case: Perform a `ls` command, followed by an `undo`.
      Expected: Added contact disappears from the list. Message indicating successful undo is printed. Subsequent `ls -a` should NOT show this contact. 

   1. Test case: Perform a `tag+` command, followed by an `undo`.
      Expected: Added contact disappears from the list. Message indicating successful undo is printed. Subsequent `ls -a` should NOT show this contact. Subsequent `ls -t` should still show the tag added with `tag+`.

   1. Test case: Perform a `tag-` command, followed by an `undo`.
      Expected: Added contact disappears from the list. Message indicating successful undo is printed. Subsequent `ls -a` should NOT show this contact. Subsequent `ls -t` should NOT still show the tag added with `tag-`. 

   1. Test case: Export data with `@`, followed by an `undo`.
      Expected: Added contact disappears from the list. Message indicating successful undo is printed. Subsequent `ls -a` should NOT show this contact. Exported data, however, should show the contact.

### Export

1. Exporting data

   1. Test case: `@ /filename data`
      Expected: File with the name `data.csv` should appear in the same directory of the application, containing all of the contacts stored in the app (which can be seen with `ls -a`).

<div style="page-break-after: always; visibility: hidden"> \pagebreak </div>

## **Appendix: Effort**

### Difficulty Level 

In our development of Hi:Re, we give the difficulty rating as **8.5 / 10**.

The reasons for our rating will be expounded on in the following sections. <br>

### Challenges Faced
<br>

#### 1.Brownfield Project

When we were first introduced to the **software architecture** of Addressbook Level-3(AB3), as well as its **source code**,
we needed time to **dissect, simplify and understand** how every single section of AB3 worked. This was
to ensure that we did not dive in immediately and develop tunnel vision for something we would like to implement that
would cause the software architecture or application to have problems down the road.

**From there, we:**

Engaged in the tutorial provided by the course coordinators to practise adding code to the existing AB3. For most of our team,
it was the first time that we were adding on to a completed project's work, which gave us confidence and helped us gain
familiarity with the source code and its various files. Through the tutorial, we were able to have a glimpse into how
a chain of files and classes worked in tandem to produce the functionality as seen when we ran the application.

#### 2. Target Audience and Implementation

**Understanding AB3 was not a 1 time process, but ongoing**. As we looked back on our initial ideas in our document, we realised 
that given our time frame for development, we were **unable to implement some features immediately** due to us still getting used
to working with pre-existing AB3 source code. Having looked at AB3 again now in detail, we had to **ideate for new features**
that our **target audience would need and want**, as well as **crossing out previous ideas that now are not feasible**.

#### 3. Version Control

For our team, this was the **first time collaborating with others using version control**. We had to familiarise ourselves with
the standard workflow process, as well as helping to **review and test each other's code** before approving Pull Requests.
During v1.3's development phase, we encountered **merge conflicts** as features inadvertently affected one another. **Bugs** also
sprouted out that could not be seen from resolving merge conflicts, and we had to push out more changes to solve these bugs.

#### Learning outcomes for our team

Through the challenges faced, we came out as **more professional, responsible, collaborative and adaptive teammates**. 
Communication was key in our project, and we are all confident to say that we have emerged stronger from the challenges,
and have become a **close-knit team that helps one another**.


### Effort Required
     
We held **weekly meetings** to discuss work updates and plans for the next week, as well as communicated frequently in our channel
about updates, help needed, suggestions and ideas.

In the process of allocating each feature to a team member to implement, each team member needed to do **documentation, development,
testing and reviewing other's work**. As aforementioned, it was **difficult to work off an existing code base** and **integrate new features**
for our target audience in a **seamless and effective manner**. In our discussions itself, we **planned meticulously** as to minimise
any form of functionality overlap between members so that development would be smooth. In the cases where we had 2 members
work on a larger feature, they **collaborated to implement the feature in parts**.

Version control mastering also **takes time and effort to get it right every time**. With each week, we helped each other with 
understanding and getting the workflow right so that our work can follow a smooth and efficient process.


### Achievements of Project

In developing Hi:Re, we are proud to have done the following given the constraints and development time frame:

  1) Built features that our target audience (HR department employees and managers) would appreciate, such as the Exporting data Command.

  2) Improved the UI and upgraded AB3's original features to cater to our target audience. (See Design Methodology Section in UG)

  3) Created an advanced tag system feature to optimise contact tag management.

  4) For security and target audience consideration, our Account features serve to allow our target audience to 
     manage multiple addressbooks should they collaborate with different companies! (Refer to Disclaimer for Account Features in UG under Commands section)

### Code Reuse

  1) In making an advanced tag feature system, we referenced AB3's storage portion to create our own taglist.

  2) With ID becoming our main identifier of contacts, we referenced how AB3 did it for names previously and also
     continued that development path when we changed the delete feature.

  3) For most of the commands that AB3 had and we kept, we upgraded them to fit our new functionality.
     In the process, `Command` and `Parser` files were used as reference for new features that we implemented.

<div style="page-break-after: always; visibility: hidden"> \pagebreak </div>

## **Appendix: Planned Enhancements**


### Number of Planned Enhancements

**Hi:Re**'s team size is **5**. As such, we will be providing 10 planned enhancements for Hi:Re. <br>


### List of Planned Enhancements
<br>

#### 1. Enforcement of Case-sensitivity for Delete command

One of our users brought up the issue of the **Delete** command being case-insensitive. For example, a contact with ID `alex123`
could be deleted by using the command `- /id AlEx123`. For commands that make amendments to the contact list, it would be
a better choice to make them case-sensitive, whilst the other commands used for displaying results (like the **Find** and **List**
commands) can have their case-insensitivity maintained. This will make it more difficult for users to accidentally perform
an unwanted deletion, which is a **serious** flaw.

#### 2. Allowing for Multiple Phone Numbers

Some personnel may have **multiple phone numbers** under their contact details, such as **Home** and **Mobile** phone numbers. As such,
in the future, we plan to add **multiple** phone numbers per contact, to allow for more flexibility. In addition, we plan
to allow HR personnel to declare which phone number is which, by allowing for phone numbers under a certain contact to be **tagged**.

This was brought up by one of our users as a significant issue, as people typically have multiple contact numbers.

#### 3. Improve the Clarity of Add Command Error (Incomplete Fields)

The current error message for the **Incomplete Fields error** for the **Add** command is long, and covers multiple lines. However, our display box has
to be kept small, in order to maximise the user's view of the entire contact list. As such, there is the issue where the
error message is cut off, which prevents users from seeing the entire error message. We plan to fix this issue by making
the error message more concise, and fit it within a single line. This will allow for users to understand the issue they are
facing and rectify it simply.

#### 4. Improve the Clarity of Add Command Error (Invalid ID)

The current error message for the **Invalid ID error** for the **Add** command is too long, and can be improved by just simply
indicating that the user can only choose lowercase alphanumeric characters for the ID. In addition to this, the system
can be made to detect which special character violated the rule, and highlight it to the user. This will streamline the
troubleshooting process for users.

#### 5. Improve the Clarity of Delete Command Error (No matching ID)

The current error message for the **No Matching ID error** for the **Delete** command is too long, and can be improved
by just simply indicating that the user can double-check the contact database to ensure that the user's ID was correctly
entered. There is some unnecessary text in the message that when removed, can greatly improve the user's experience and
hence the troubleshooting procedure.

#### 6. Improve the Clarity of Delete Command Error (Incomplete Fields)

The current error message for the **Incomplete Fields error** for the **Delete** command is long, and covers multiple lines.
However, our display box has to be kept small, in order to maximise the user's view of the entire contact list. As such,
there is the issue where the error message is cut off, which prevents users from seeing the entire error message. We 
plan to fix this issue by making the error message more concise, and fit it within a single line. This will allow for users
to understand the issue they are facing and rectify it simply.

#### 7. Improve the Clarity of Delete Command Error (Invalid ID)

Like **4.**, The current error message for the **Invalid ID error** for the **Delete** command is too long, and can be
improved by just simply indicating that the user can only choose lowercase alphanumeric characters for the ID. In addition
to this, the system can be made to detect which special character violated the rule, and highlight it to the user.
This will streamline the troubleshooting process for users.

#### 8. Enabling Redo and Undo Function for Other Commands

In order to implement a **true** **Redo and Undo** feature to Hi:Re, the feature should be able to **redo and undo** all
modifications to the contact list, bar some commands such as our **Export** feature. The current lack of this feature is
an issue, and implementing it will add to the completeness of our application. We plan to introduce this to **Hi:Re** as
soon as possible.

#### 9. Allow for Special Characters in Name Field

As brought up by multiple users of **Hi:Re**, our applications `name` field for contacts does not permit the usage of
special characters, which might be an issue when certain personnel have special characters in their name. One such example
brought up by one of our users is the presence of **'S/O'** or **'D/O'** in many indian names. The forward slash character `/`
is currently not permitted to be used in the name field.

In order to maintain inclusivity, we plan to allow for special characters in our name field in the next iteration of **Hi:Re**.

#### 10. Toggle Display Command Fix when List Command is used

The current **Toggle Display** command in **Hi:Re** has some minor flaws. When the display is toggled off, the user will
still be unable to view contacts in their contact list, even after using commands such as `ls -a`, which is supposed to list
all contacts in the contact database. As a result, this may confuse the user, and make them think that there are no contacts
available in the contact database.

We plan to fix this issue in the next iteration of **Hi:Re** in order to provide more clarity of use to our dedicated users.

