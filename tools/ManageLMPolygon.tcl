catch [destroy .c]

# The master list of x points
set allx {}

# The master list of y points
set ally {}

# The names of each polyline
set allNames {}

# The start index of each polyline
set allStartIdx {}

# A counter for insertion into allStartIdx
set allIdx 0


proc addToMasterList {x y description} {
    global allx ally allNames allStartIdx allIdx

    lappend allNames $description
    lappend allStartIdx $allIdx
    for {set idx 0} {$idx < [llength $x]} {incr idx} {
        lappend allx [lindex $x $idx]
        lappend ally [lindex $y $idx]
    }
    incr allIdx $idx
}

# These points are all sampled off an image of the LM.

# Ascent Stage outline
set x {98 86 86 97 104 191 192 203 222 222 207 185 185 180 180 167 98}
set y {118 99 79 66 30 29    73 73 84 104 115 115 118 118 115 118 118 }
#drawPolyline .c $x $y
addToMasterList $x $y {Ascent Stage I}

set x {127 135 154 162 }
set y {118 44 44 118 }
#drawPolyline .c $x $y
addToMasterList $x $y {Ascent Stage II}

set x {131 157}
set y {76  76}
#drawPolyline .c $x $y
addToMasterList $x $y {Ascent Stage III}

set x {132 119 134}
set y {68 57 52}
#drawPolyline .c $x $y
addToMasterList $x $y {Ascent Stage IV}

#set x {156 169 154}
set x {157 169 155}
set y {68 57 52}
#drawPolyline .c $x $y
addToMasterList $x $y {Ascent Stage V}

#set x {128 114 106 106 117 144 171 182 182 174 160}
set x {128 114 106 106 117 144 171 182 182 174 161}
set y {110 98 84  65 49 38 49 65 84 98 110}
#drawPolyline .c $x $y
addToMasterList $x $y {Ascent Stage VI}

# Left pad
set x { 6 32 }
set y { 205 205 }
#drawPolyline .c $x $y
addToMasterList $x $y {Left pad}

# Right pad
set x { 282 256 }
set y { 205 205 }
#drawPolyline .c $x $y
addToMasterList $x $y {Right pad}

# Left leg
set x {20 54 80}
set y {205 131 121}
#drawPolyline .c $x $y
addToMasterList $x $y {Left leg I}

set x {31 80 55}
set y {180 170 132}
#drawPolyline .c $x $y
addToMasterList $x $y {Left leg II}

# Right leg
set x {268 234 208}
set y {205 131 121}
#drawPolyline .c $x $y
addToMasterList $x $y {Right leg I}

set x {257 208 233}
set y {180 170 132}
#drawPolyline .c $x $y
addToMasterList $x $y {Right leg II}

# Frame
set x {80 208 208 80 80}
set y {118 118 171 171 118}
#drawPolyline .c $x $y
addToMasterList $x $y {Frame I}

set x {91 91 197 197}
set y {171 178 178 171}
#drawPolyline .c $x $y
addToMasterList $x $y {Frame II}

set x {122 122}
set y {118 171}
#drawPolyline .c $x $y
addToMasterList $x $y {Frame III}

set x {166 166}
#drawPolyline .c $x $y
addToMasterList $x $y {Frame IV}

# Engine bell
set x {128 124 164 160}
set y {178 198 198 178}
#drawPolyline .c $x $y
addToMasterList $x $y {Engine Bell}

# Exhaust flame
set x {124 144 164}
set y {198 198 198}
addToMasterList $x $y {Exhaust Flame}


proc drawPolyline {w x y fromidx toidx} {
    set idx $fromidx
    incr idx
    set last $fromidx
    while {$idx <= $toidx} {
         $w create line [lindex $x $last] [lindex $y $last] [lindex $x $idx] [lindex $y $idx]   -fill "green" 
         incr idx
         incr last
    }
}

proc drawMultiplePolylines {w x y allStartIdx allNames} {
    set polylineidx 0
    set nextpolylineidx 1
    while {$nextpolylineidx < [llength $allStartIdx]} {
        drawPolyline $w $x $y [lindex $allStartIdx $polylineidx] [expr {[lindex $allStartIdx $nextpolylineidx] - 1}]
        incr polylineidx
        incr nextpolylineidx
    }
    
    drawPolyline $w $x $y [lindex $allStartIdx $polylineidx] [expr {[llength $x] - 1}]
}
        
# Determine the min and max value in a list.
proc getBounds {x} {
    set min [lindex $x 0]
    set max $min
    
    foreach o $x {
        if {$o < $min} {
            set min $o
        }
        if {$o > $max} {
            set max $o
        }
    }
    
    return [list $min $max]
}

# Add bias to each element in the list
proc applybias {xname bias} {
    upvar 1 $xname x
    set newx {}
    
    foreach elem $x {
        lappend newx [expr {$elem + $bias}]
    }
    
    set x $newx
}

# Multiply mul into each element in the list
proc applyscale {xname mul} {
    upvar 1 $xname x
    set newx {}
    
    foreach elem $x {
        lappend newx [expr {$elem * $mul}]
    }
    
    set x $newx
}

console show
canvas .c -bg "black"
grid .c -sticky nsew
grid columnconfigure . 0 -weight 1
grid rowconfigure . 0 -weight 1
# drawMultiplePolylines .c $allx $ally $allStartIdx $allNames

proc absMax {l} {
    set rv 0
    foreach i $l {
        set abs [expr {abs($i)}]
        if {$abs > $rv} {
            set rv $abs
        }
    }
    return $rv
}

proc normalizeAndCenter {xname yname} {
    upvar 1 $xname allx
    upvar 1 $yname ally
    
    foreach {minx maxx} [getBounds $allx] {break}
    set centerx [expr {($minx + $maxx) / 2}]
    puts "minx = $minx maxx = $maxx centerx = $centerx"
    
    foreach {miny maxy} [getBounds $ally] {break}
    set centery [expr {($miny + $maxy) / 2}]
    puts "miny = $miny maxy = $maxy centery = $centery"
    
    applybias allx [expr {-$centerx}]
    applybias ally [expr {-$centery}]
    
    puts "After centering..."
    foreach {minx maxx} [getBounds $allx] {break}
    set centerx [expr {($minx + $maxx) / 2}]
    puts "minx = $minx maxx = $maxx centerx = $centerx"
    
    foreach {miny maxy} [getBounds $ally] {break}
    set centery [expr {($miny + $maxy) / 2}]
    puts "miny = $miny maxy = $maxy centery = $centery"
    
    # Set the single largest dimension as 1.
    set norm [absMax [list $minx $miny $maxx $maxy]]
    puts "norm = $norm"
    
    set scale [expr {1.0 / $norm}]
    puts "scale = $scale"
    
    applyscale allx $scale
    applyscale ally $scale
    
}

normalizeAndCenter allx ally

proc drawScaledAndRotated {w xarray yarray allStartIdx allNames centerx centery angleDeg scale} {
    set angle [expr {$angleDeg * 3.14159265359 / 180.0}]
    set nangle [expr {-1 * $angle}]
    
    # Perform rotation and scaling
    set idx 0
    set newx {}
    set newy {}
    set len [llength $xarray]
    
    while {$idx < $len} {
        set x [lindex $xarray $idx]
        set y [lindex $yarray $idx]
        
        lappend newx [expr {$scale * ($x * cos($nangle) - $y * sin($nangle)) + $centerx}]
        lappend newy [expr {$scale * ($x * sin($nangle) + $y * cos($nangle)) + $centery}]
        incr idx
    }
    
    $w delete all
    drawMultiplePolylines $w $newx $newy $allStartIdx $allNames   
}

# drawScaledAndRotated .c $allx $ally $allStartIdx $allNames 150 150 45 150

proc doDraw {centerx centery angle scale} {
    global allx ally allStartIdx allNames 
    drawScaledAndRotated .c $allx $ally $allStartIdx $allNames $centerx $centery $angle $scale
}

proc spin {} {
    while {1} {
        for {set i 0} {$i < 360} {incr i 20} {
            doDraw 200 200 $i 150
            update
            after 80
        }
    }
}

proc writeJavaCode {} {
    global allx ally allStartIdx allNames
    set xcode "private double allx\[\]\[\] = \{\n"
    set ycode "private double ally\[\]\[\] = \{\n"
    
    set allEndIdx {}
    foreach idx $allStartIdx {
        if {$idx != 0} {
            lappend allEndIdx [expr {$idx-1}]
        }
    }
    lappend allEndIdx [expr {[llength $allx] - 1}]
    
    set polylineidx 0
    set pointidx 0
    
    for {set polylineidx 0} {$polylineidx < [llength $allStartIdx]} {incr polylineidx} {
        
        set name [lindex $allNames $polylineidx]
        append xcode "\t// $name\n\t\{ "
        append ycode "\t// $name\n\t\{ "
        
        while {$pointidx <= [lindex $allEndIdx $polylineidx]} {
            append xcode [lindex $allx $pointidx]
            append ycode [lindex $ally $pointidx]
            if {$pointidx != [lindex $allEndIdx $polylineidx]} {
                append xcode ", "
                append ycode ", "
            } else {
                set name [lindex $allNames $polylineidx]
                append xcode " \},\n"
                append ycode " \},\n"
            }
            incr pointidx
        }
    }
    set xcode [string replace $xcode [string last "," $xcode] end "\n\};"]
    set ycode [string replace $ycode [string last "," $ycode] end "\n\};"]
    
    puts "$xcode\n\n$ycode"
}

    

