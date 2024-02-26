# Heater

**Heater** is a straightforward tech mod about centralizing fuel consumption and how to propagate the produced heat to nearby furnaces and furnaces-adjacent blocks.

![Heater Showcase](img/Heater_Showcase_1.png)

(Credits to [StarOcean](https://github.com/0Starocean0) for the fantastic textures they created!)

It only adds three blocks: the namesake **Heater**, the **Heat Pipe**, and the new and shiny **Thermostat**.

Here's what they do.

### Heater

As its namesake block, the **Heater** is this mod's core. Its role is to burn fuel and propagate the generated heat to nearby compatible blocks, such as Furnaces, Blast Furnaces, and Smokers for vanilla Minecraft, but also [Haunt Furnaces](https://modrinth.com/mod/haunt-furnace), [Kilns](https://modrinth.com/mod/embers-kiln), and probably others.

<details>
<summary>Heater Interface</summary>

![Heater Interface](img/Heater_Screen.png)

</details>

<details>
<summary>Heater Recipe</summary>

![Heater Recipe](img/Heater_Recipe.png)

</details>

### Heat Pipe

The **Heat Pipe** is a pipe-like block which works as you may imagine. Heat produced by a Heater can travel through it, basically extending the Heater's reach. But be aware that heat can only propagate a certain distance before dissipating, and the oxidization state of the Heat Pipe can only reduce said distance.

<details>
<summary>Heat Pipe Recipe</summary>

![Heat Pipe Recipe](img/Heat_Pipe_Recipe.png)

</details>

The Heat Pipe is entity-free, so you can fully fulfil your pipe dreams without concerns about dropping performances.

### Thermostat

Only from version 2.0 forward.

The **Thermostat** allows heat to propagate only in the direction it is facing and only if powered. When unpowered, it acts like any other heat-inert block.

The Thermostat is also the only block that can "push" heat into a Heater, doubling as a heat repeater of sorts.

<details>
<summary>Thermostat Recipe</summary>

![Thermostat Recipe](img/Thermostat_Recipe.png)

</details>

The Thermostat is entity-free, too.

## Planned Features

In the next major version (i.e. 3.x), other than upgrading the target Minecraft version to 1.20.3+, I will try to implement a configuration file to customize some numbers that I thus far arbitrarily set and to extend the compatibility options with other mods.

I plan to add dyeable Heat Pipes (and maybe Thermostats, too) with some color-matching-heat-propagation logic. However, considering that each new block has four oxidization stages and as many waxed variants, I am not so thrilled to implement SIXTEEN new blocks with all their SIXTY-FOUR variants. Double that if I decide to dye the Thermostat, too. So, I don't promise anything.
