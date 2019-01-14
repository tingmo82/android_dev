#ifndef ANDROID_TINGSERVICE_H
#define ANDROID_TINGSERVICE_H

#include <stdint.h>

#include <binder/Parcel.h>

#include <BnTingService.h>

namespace android {

class TingService : public BnTingService
{
public:
    static void instantiate();

    // Real implemntation of ITingService
    virtual status_t test_function(const char *str);
    
	virtual status_t onTransact( uint32_t code,
            const Parcel& data,
            Parcel* reply,
            uint32_t flags);

private:
    TingService();
    virtual ~TingService();
};

}; // namespace android

#endif //ANDROID_TINGSERVICE_H
