package me.kubbidev.nexuspowered.serialize;

import java.util.Base64;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

final class Base64Util {

    private Base64Util() {
    }

    public static String encode(byte[] buf) {
        return Base64.getEncoder().encodeToString(buf);
    }

    public static byte[] decode(String src) {
        try {
            return Base64.getDecoder().decode(src);
        } catch (IllegalArgumentException e) {
            // compat with the previously used base64 encoder
            try {
                return Base64Coder.decodeLines(src);
            } catch (Exception ignored) {
                throw e;
            }
        }
    }
}