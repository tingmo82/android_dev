#include <stdint.h>

#include <utils/Log.h>
#include <binder/Parcel.h>

#include <BnTingService.h>

namespace android {

status_t BnTingService::onTransact( uint32_t code,
        const Parcel& data,
        Parcel* reply,
        uint32_t flags)
{
    ALOGI("%d: %s CODE = %d", __LINE__, __FUNCTION__, code);

    switch(code)
    {
        case TING_TEST_FUNC:
        {
            // Check if this request is alright
            CHECK_INTERFACE(ITingService, data, reply);
            
            // Read string from user(proxy)
            const char *str = data.readCString();
            
            reply->writeInt32(test_function(str));

            return NO_ERROR;
        } break;

        default:
            return BBinder::onTransact(code, data, reply, flags);
    }
}

}; // namespace android
