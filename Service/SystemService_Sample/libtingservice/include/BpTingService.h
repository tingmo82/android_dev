#ifndef ANDROID_BPTINGSERVICE_H
#define ANDROID_BPTINGSERVICE_H

#include <stdint.h>

#include <utils/RefBase.h>
#include <binder/Parcel.h>

#include <ITingService.h>

namespace android {

class BpTingService : public BpInterface<ITingService>
{
public:
    BpTingService(const sp<IBinder>& impl);

    virtual status_t test_function(const char *str);
};

}; // namespace android

#endif //ANDROID_BPTINGSERVICE_H
