#ifndef ANDROID_BNTINGSERVICE_H
#define ANDROID_BNTINGSERVICE_H

#include <stdint.h>

#include <utils/RefBase.h>
#include <binder/Parcel.h>

#include <ITingService.h>

namespace android {

class BnTingService : public BnInterface<ITingService>
{
public:
	virtual status_t onTransact( uint32_t code,
            const Parcel& data,
            Parcel* reply,
            uint32_t flags = 0);
};

}; // namespace android

#endif //ANDROID_ITINGSERVICE_H
