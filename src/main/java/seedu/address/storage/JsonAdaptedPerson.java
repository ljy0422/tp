package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
//import seedu.address.model.person.Address;
//import seedu.address.model.person.Email;
import seedu.address.model.person.Id;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private String name;
    private String id;
    private String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    //    /**
    //     * Constructs a {@code JsonAdaptedPerson} with the given person details.
    //    */
    //    @JsonCreator
    //    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("id") String id,
    //                             @JsonProperty("phone") String phone,
    //            @JsonProperty("email") String email, @JsonProperty("address") String address,
    //            @JsonProperty("tags") List<JsonAdaptedTag> tags
    //                             ) {
    //        this.name = name;
    //        this.id = id;
    //        this.phone = phone;
    //        this.email = email;
    //        this.address = address;
    //        if (tags != null) {
    //            this.tags.addAll(tags);
    //        }
    //    }

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("id") String id,
                             @JsonProperty("phone") String phone,
        //@JsonProperty("email") String email, @JsonProperty("address") String address,
        @JsonProperty("tags") List<JsonAdaptedTag> tags
    ) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.email = "test";
        this.address = "test";
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        String nameBeforeEncryption = source.getName().fullName;
        String hpBeforeEncryption = source.getPhone().value;
        //email = source.getEmail().value;
        //address = source.getAddress().value;
        id = source.getId().value;
        tags.addAll(source.getTags().stream()
               .map(JsonAdaptedTag::new)
               .collect(Collectors.toList()));
        String idBeforeExcryption = id;
        try {
            String encryptedName = FixedAesUtil.encrypt(nameBeforeEncryption);
            String encryptedPhone = FixedAesUtil.encrypt(hpBeforeEncryption);
            String encryptedId = FixedAesUtil.encrypt(idBeforeExcryption);
            this.name = encryptedName;
            this.phone = encryptedPhone;
            this.id = encryptedId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        email = "test";
        address = "test";

    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }

        String decryptedName = "";
        try {
            decryptedName = FixedAesUtil.decrypt(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Name.isValidName(decryptedName)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(decryptedName);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }

        String decryptedPhone = "";
        try {
            decryptedPhone = FixedAesUtil.decrypt(phone);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Phone.isValidPhone(decryptedPhone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(decryptedPhone);

        //if (email == null) {
        //    throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        //}
        //if (!Email.isValidEmail(email)) {
        //    throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        //}
        //final Email modelEmail = new Email(email);

        //if (address == null) {
        //    throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
        //    Address.class.getSimpleName()));
        //}
        //if (!Address.isValidAddress(address)) {
        //    throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        //}
        //final Address modelAddress = new Address(address);

        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Id.class.getSimpleName()));
        }

        String decryptedId = "";
        try {
            decryptedId = FixedAesUtil.decrypt(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Id.isValidId(decryptedId)) {
            throw new IllegalValueException(Id.MESSAGE_CONSTRAINTS);
        }
        final Id modelId = new Id(decryptedId);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        return new Person(modelName, modelId, modelPhone, modelTags);
    }

}
