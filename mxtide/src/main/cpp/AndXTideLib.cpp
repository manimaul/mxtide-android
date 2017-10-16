/*
 * AndXTideLib.cpp
 *
 *  Created on: Mar 28, 2013
 *  Author: Will Kamp - manimaul@gmail.com
 *
 */

#include "AndXTideLib.hh"

StationIndex si;
Dstr data;

Unit unit = meters;

void
Java_com_mxmariner_andxtidelib_XtideJni_loadHarmonics( JNIEnv *env, jobject obj, jstring pPath)
{
	const char *nPath =(*env).GetStringUTFChars(pPath, NULL);
	loadHarmonics(nPath);
	(*env).ReleaseStringUTFChars(pPath, nPath);
}

void
Java_com_mxmariner_andxtidelib_XtideJni_setUnits( JNIEnv *env, jobject obj, jint pUnit)
{
	if (pUnit == feet) {
		unit = feet;
	} else if (pUnit == meters) {
		unit = meters;
	}
}

jobjectArray
Java_com_mxmariner_andxtidelib_XtideJni_getStationIndex( JNIEnv *env, jobject obj )
{
	return getStationIndex(env);
}

jstring
Java_com_mxmariner_andxtidelib_XtideJni_getStationAbout( JNIEnv *env, jobject obj, jstring pStationName, jlong pEpoch )
{
	const char *nName = (*env).GetStringUTFChars(pStationName, NULL);
	long epoch = (long) pEpoch;
	getAbout(nName, epoch);
     return (*env).NewStringUTF(data.utf8().aschar());
}

jstring
Java_com_mxmariner_andxtidelib_XtideJni_getStationGraphSvg( JNIEnv *env, jobject obj, jstring pStationName, jlong pEpoch )
{
	const char *nName = (*env).GetStringUTFChars(pStationName, NULL);
	long epoch = (long) pEpoch;
	getGraph(nName, epoch);
     return (*env).NewStringUTF(data.utf8().aschar());
}

jstring
Java_com_mxmariner_andxtidelib_XtideJni_getStationClockSvg( JNIEnv *env, jobject obj, jstring pStationName, jlong pEpoch )
{
	const char *nName = (*env).GetStringUTFChars(pStationName, NULL);
	long epoch = (long) pEpoch;
	getClock(nName, epoch);
     return (*env).NewStringUTF(data.utf8().aschar());
}

jstring
Java_com_mxmariner_andxtidelib_XtideJni_getStationRawData( JNIEnv *env, jobject obj, jstring pStationName, jlong pEpoch )
{
	const char *nName = (*env).GetStringUTFChars(pStationName, NULL);
	long epoch = (long) pEpoch;
	getData(nName, epoch, Mode::raw);
	return (*env).NewStringUTF(data.utf8().aschar());
}

jstring
Java_com_mxmariner_andxtidelib_XtideJni_getStationPlainData( JNIEnv *env, jobject obj, jstring pStationName, jlong pEpoch )
{
	const char *nName = (*env).GetStringUTFChars(pStationName, NULL);
	long epoch = (long) pEpoch;
	getData(nName, epoch, Mode::plain);
	return (*env).NewStringUTF(data.utf8().aschar());
}

jstring
Java_com_mxmariner_andxtidelib_XtideJni_getStationPrediction( JNIEnv *env, jobject obj, jstring pStationName, jlong pEpoch )
{
	const char *nName = (*env).GetStringUTFChars(pStationName, NULL);
	long epoch = (long) pEpoch;
	getPrediction(nName, epoch);
	return (*env).NewStringUTF(data.utf8().aschar());
}

jstring
Java_com_mxmariner_andxtidelib_XtideJni_getStationTimestamp( JNIEnv *env, jobject obj, jstring pStationName, jlong pEpoch )
{
	const char *nName = (*env).GetStringUTFChars(pStationName, NULL);
	long epoch = (long) pEpoch;
	getTimestamp(nName, epoch);
	return (*env).NewStringUTF(data.utf8().aschar());
}

jobjectArray getStationIndex(JNIEnv *env) 
{
    jobjectArray ret = (jobjectArray) (*env).NewObjectArray(si.size(),(*env).FindClass("java/lang/String"),NULL);
    jstring js;
	for (unsigned long i=0; i<si.size(); ++i) {
		si.operator[](i)->name.aschar();
		data = si.operator [](i)->name.aschar();
		data += ";";
		data += si.operator [](i)->coordinates.lat();
		data += ";";
		data += si.operator [](i)->coordinates.lng();
		data += ";";
		if (si.operator [](i)->isCurrent) {
			data += "current";
		} else {
			data += "tide";
		}
		
        js = (*env).NewStringUTF(data.utf8().aschar());
        (*env).SetObjectArrayElement(ret,i,js);
        (*env).DeleteLocalRef(js);
	}
    return ret;
}

void getAbout(Dstr station, long epoch) 
{
	StationRef *sr = si.getStationRefByName(station);
	Station *sa = sr->load();

	if (unit == meters) {
		sa->setUnits(Units::meters);
	} else {
		sa->setUnits(Units::feet);
	}

	Timestamp ts = Timestamp(epoch);

	data = "";
	sa->print(data, ts, ts, Mode::about, Format::text);
}

void getPrediction(Dstr station, long epoch) 
{
	StationRef *sr = si.getStationRefByName(station);
	Station *sa = sr->load();

	if (unit == meters) {
		sa->setUnits(Units::meters);
	} else {
		sa->setUnits(Units::feet);
	}

	Timestamp ts = Timestamp(epoch);

	PredictionValue value = sa->predictTideLevel(ts);

	data = "";
	value.print(data);
}

void getClock(Dstr station, long epoch) 
{
	StationRef *sr = si.getStationRefByName(station);
	Station *sa = sr->load();

	if (unit == meters) {
		sa->setUnits(Units::meters);
	} else {
		sa->setUnits(Units::feet);
	}
	Timestamp ts = Timestamp(epoch);

	data = "";
	sa->print(data, ts, ts, Mode::clock, Format::SVG);
}

void getGraph(Dstr station, long epoch) 
{
	StationRef *sr = si.getStationRefByName(station);
	Station *sa = sr->load();

	if (unit == meters) {
		sa->setUnits(Units::meters);
	} else {
		sa->setUnits(Units::feet);
	}
	Timestamp ts = Timestamp(epoch);

	data = "";
	sa->print(data, ts, ts, Mode::graph, Format::SVG);
}

void getData(Dstr station, long epoch, Mode::Mode mode) 
{
	StationRef *sr = si.getStationRefByName(station);
	Station *sa = sr->load();
	if (unit == meters) {
		sa->setUnits(Units::meters);
	} else {
		sa->setUnits(Units::feet);
	}

	struct tm morning;
	struct tm evening;

	//time(&rawtime);
	morning = *localtime(&epoch);
	evening = *localtime(&epoch);

	//rewind to the begining of the day
	morning.tm_hour = 0;
	morning.tm_min = 0;
	morning.tm_sec = 0;

	//fast forward to the end of the day
	evening.tm_hour = 24;
	evening.tm_min = 0;
	evening.tm_sec = 0;

	Timestamp starttime = Timestamp(mktime(&morning));
	Timestamp endtime = Timestamp(mktime(&evening));

	data = "";
	sa->print(data, starttime, endtime, mode, Format::text);
}

void getTimestamp(Dstr station, long epoch) 
{
	StationRef *sr = si.getStationRefByName(station);
	Station *sa = sr->load();
	Timestamp t = Timestamp(epoch);

	data = "";
	t.print(data, sa->timezone);
}

void loadHarmonics(const char* path) 
{
	si.addHarmonicsFile(path);
}
