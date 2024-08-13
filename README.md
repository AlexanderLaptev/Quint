# Quint

Quint (i.e., perfect fifth) is a simple sound synthesis library written in Kotlin for the Java platform utilizing the
Java Sound API.

# Highlights

- Lightweight: built entirely upon the Java Sound API
- Highly extendable
- Supports mono and stereo audio
- Supports both synchronous and asynchronous types of playback
- Ability to write bytes directly to a byte buffer
- Ability to play simple sequences of notes or chords
- Out-of-the-box ADSR envelopes, LFOs, and oscillators (sine, triangle, square, sawtooth, noise)

# Usage

Take a look at the tests to get a feeling for how this library works. Every test is self-contained and can run by
itself. Feel free to check the KDocs, since every part of the public API is documented.

# License

This library is licensed under the MIT license. More infor can be found in `LICENSE.md`.

# Notes

- The library uses `Double` instead of `Float` for precision reasons. During testing, `Float`s were giving artifacts
  after ~20 seconds of continuous playing. Therefore, it has been decided to switch to `Double` as it has enough
  precision for the job. During experimenting, it seemed like the synthesis worked as intended for at least 2*10<sup>
  15</sup> frames of 44.1 kHz, 16-bit audio (~143 years).
- Even though there are multiple tests, I cannot guarantee that there will be no bugs. Feel free to report any bugs you
  encounter with this library.
- While I did not benchmark the library, it seems pretty fast for generating sound through synthesizers in real time (
  see `SynthesizerTest.kt` for an example). If you intend to do synthesis in real-time, make sure you're using a
  sufficiently small buffer for your `SourceDataLine`s.
