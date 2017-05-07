# Thorondor

Thorondor is a Scala pipeline that transforms video data into meaningful analysis.
Not technically a [Vala](https://github.com/kokellab/valar), Thorondor is the mightiest Great Eagle and an inhabitant of [Valinor](https://github.com/kokellab/valinor) in the First Age.

## Constituents

Thorondor consists of three major subprojects that have some overlap.

### Lorien
Processes video data from [SauronX](https://github.com/kokellab/sauronx) to generate features like motion index.
Lorien is a Vala who is the “Master of Visions and Dreams”.

### Sorontar

As the heart of Thorondor, analyzes features that Lorien produces.
Calculates statistics, subtracts controls, and corrections for parameters like temperature and time of day.
_Sorontar_ is another name for _Thorondor_.

### Mandos

Handles the cheminformatics side: target prediction other requirements.
This is mostly alongside Lorien and upstream of Sorontar.
Mandos is a Vala who is the judge of spirits and the brother of Lorien.
