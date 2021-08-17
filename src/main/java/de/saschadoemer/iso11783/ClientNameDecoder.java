package de.saschadoemer.iso11783;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Client name decoder for ISO 11783-5 encoded client names.
 */
public final class ClientNameDecoder {

    /**
     * Hide the constructor.
     */
    private ClientNameDecoder() {
    }

    /**
     * Decode a given binary representation of a client name into a more common representation.
     *
     * @param clientName -
     */
    public static void decode(byte[] clientName) {
        if (null == clientName || clientName.length == 0) {
            throw new IllegalArgumentException("The client name can not be null or empty");
        }
        validateClientName(clientName);
        ByteBuffer byteBuffer = ByteBuffer.wrap(clientName);
    }

    private static void validateClientName(byte[] clientName) {
        final String hexRepresentation = new String(clientName);
        final Pattern pattern = Pattern.compile("^[A-Fa-f0-9]{16}$");
        final Matcher matcher = pattern.matcher(hexRepresentation);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("The HEX representation '%s' does not match the requirements, please check the input.", hexRepresentation));
        }
    }

    /**
     * Read the device class from the client name.
     *
     * @param clientName -
     * @return -
     */
    static boolean getSelfConfigurableAddress(byte[] clientName) {
        return ((getByte(clientName, 7) >> 7) & 0b1) == 1;
    }

    /**
     * Read the industry group from the client name.
     *
     * @param clientName -
     * @return -
     */
    static int getIndustryGroup(byte[] clientName) {
        return (getByte(clientName, 7) >> 4) & 0b111;
    }

    /**
     * Read the device class instance from the client name.
     *
     * @param clientName -
     * @return -
     */
    static int getDeviceClassInstance(byte[] clientName) {
        return getByte(clientName, 7) & 0b1111;
    }

    /**
     * Read the device class from the client name.
     *
     * @param clientName -
     * @return -
     */
    static int getDeviceClass(byte[] clientName) {
        return getByte(clientName, 6) >> 1;
    }

    /**
     * Read the function from the client name.
     *
     * @param clientName -
     * @return -
     */
    static int getFunction(byte[] clientName) {
        return Byte.toUnsignedInt(getByte(clientName, 5));
    }

    /**
     * Read the function instance from the client name.
     *
     * @param clientName -
     * @return -
     */
    static int getFunctionInstance(byte[] clientName) {
        return (getByte(clientName, 4) >> 3) & 0b11111;
    }

    /**
     * Read the ECU instance from the client name.
     *
     * @param clientName -
     * @return -
     */
    static int getECUInstance(byte[] clientName) {
        return getByte(clientName, 4) & 0b111;
    }

    /**
     * Read the manufacturer code from the client name.
     *
     * @param clientName -
     * @return -
     */
    static int getManufacturerCode(byte[] clientName) {
        return getByte(clientName, 3) << 3 | 0b111 & (getByte(clientName, 2) >> 5);
    }

    /**
     * Read the identity number from the client name.
     *
     * @param clientName -
     * @return -
     */
    static int getIdentityNumber(byte[] clientName) {
        return Byte.toUnsignedInt(getByte(clientName, 0))
                | Byte.toUnsignedInt(getByte(clientName, 1)) << 8
                | (0b11111 & getByte(clientName, 2)) << 16;
    }

    private static byte getByte(byte[] clientName, int position) {
        final BigInteger i = new BigInteger(new String(clientName).substring(0, 8), 16);
        final BigInteger j = new BigInteger(new String(clientName).substring(8, 16), 16);
        final ByteBuffer newByteBuffer = ByteBuffer.allocate(8);
        newByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        newByteBuffer.put(getUInt32LE(j));
        newByteBuffer.put(getUInt32LE(i));
        return newByteBuffer.get(position);
    }

    private static byte[] getUInt32LE(BigInteger bigInteger) {
        final ByteBuffer newByteBuffer = ByteBuffer.allocate(4);
        newByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        newByteBuffer.putInt(bigInteger.intValue());
        return newByteBuffer.array();
    }

}
