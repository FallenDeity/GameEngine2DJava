package engine.renderer;

import org.lwjgl.openal.AL10;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {
	private final int bufferId;
	private final int sourceId;
	private final String path;
	private boolean isPlaying = false;

	public Sound(String path, boolean loop) {
		this.path = path;
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);
		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(path, channelsBuffer, sampleRateBuffer);
		if (rawAudioBuffer == null) {
			System.err.println("Failed to load audio file " + path + ". " + "Reason: " + stb_vorbis_decode_filename(path, channelsBuffer, sampleRateBuffer));
			stackPop();
			stackPop();
			bufferId = sourceId = -1;
			return;
		}
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		stackPop();
		stackPop();
		int format = channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
		bufferId = AL10.alGenBuffers();
		AL10.alBufferData(bufferId, format, rawAudioBuffer, sampleRate);
		sourceId = AL10.alGenSources();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
		AL10.alSourcei(sourceId, AL10.AL_POSITION, 0);
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 0.4f);
		free(rawAudioBuffer);
	}

	public void delete() {
		AL10.alDeleteSources(sourceId);
		AL10.alDeleteBuffers(bufferId);
	}

	public void play() {
		int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
		if (state == AL10.AL_STOPPED) {
			AL10.alSourcei(sourceId, AL10.AL_POSITION, 0);
			isPlaying = false;
		}
		if (!isPlaying) {
			AL10.alSourcePlay(sourceId);
			isPlaying = true;
		}
	}

	public void stop() {
		if (isPlaying) {
			AL10.alSourceStop(sourceId);
			isPlaying = false;
		}
	}

	public boolean isPlaying() {
		int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
		isPlaying = state == AL10.AL_PLAYING;
		return isPlaying;
	}

	public String getPath() {
		return path;
	}
}
