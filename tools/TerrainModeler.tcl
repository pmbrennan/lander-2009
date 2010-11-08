#######################################################################
#
# TerrainModeler.tcl
#
# A simple modeler for Lunar Lander 2D
#
# Models the terrain as a series of points, with line segments
# between.
#
#######################################################################

#######################################################################
#
# Initialization : show the console and destroy any existing display.
#
# This only works for Windows?
#
#console show

#######################################################################
#
# CONSTANTS
#
set pi          3.1415926536
set pi_over_2   1.5707963268
set pi_times_2  6.2831853072

# Radius of the moon in meters
set rMoon 1737400

#######################################################################
#
# The model
#
set M {}

#######################################################################
#
# fromThetaR
#
# Create a terrain point given:
# an angle in radians
# a radius in meters
#
proc fromThetaR {theta r} {
    # the list is in this form:
    # [theta:angle from origin    r:distance from center
    #  d:downrange distance from origin    alt:altitude
    #  x:horizontal distance    y:vertical distance]
    set d [expr {-1.0 * $theta * $::pi * $r} ]
    #puts "d = $d"
    set alt [expr {$r - $::rMoon}]
    #puts "alt = $alt"
    set x [expr {$r * cos($theta)}]
    #puts "x = $x"
    set y [expr {$r * sin($theta)}]
    #puts "y = $y"

    return [list $theta $r $d $alt $x $y]
}

#######################################################################
#
# fromXY
#
proc fromXY {x y} {
    set theta [expr {atan2($y,$x)}]
    if {$theta < 0} {
        set theta [expr {$theta + $::pi_times_2}]
    }
    set r [expr {sqrt($x * $x + $y * $y)} ]

    set d [expr {-1.0 * $theta * $::pi * $r} ]
    set alt [expr {$r - $::rMoon}]

    return [list $theta $r $d $alt $x $y]
}

#######################################################################
#
# fromTRadAlt
#
# Create a terrain point given:
# an angle in radians
# an altitude in meters above the datum circle
#
proc fromTRadAlt {thetaRad alt} {
    set r [expr {$alt + $::rMoon}]
    return [fromThetaR $thetaRad $r]
}

#######################################################################
#
# fromTDegAlt
#
# Create a terrain point given:
# an angle in degrees
# an altitude in meters above the datum circle
#
proc fromTDegAlt {thetaDeg alt} {
    set r [expr {$alt + $::rMoon}]
    set theta [expr {$thetaDeg * $::pi / 180.0}]
    return [fromThetaR $theta $r]
}

#######################################################################
#
# makeInitialModel
#
# Create an initial model profile, consisting of 360 points (1 degree
# apart) all at altitude=0 meters.
#
proc makeInitialModel {} {
    for {set t 0} {$t < 360} {incr t 60} {
        lappend rv [fromTDegAlt $t 0]
    }
    return $rv
}

#######################################################################
#
# readModelFromChannel
#
# Read in a model from a specified channel
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# channelId : the channel to read from.
#
proc readModelFromChannel {modelVarName channelId} {
    upvar 1 $modelVarName model
    set model {}
    set data [read $channelId]

    foreach line [split $data \n] {
        if {$line == ""} {continue}
        regsub -all {,} $line " " lineNoCommas
        #puts "in = {$line} out = {$lineNoCommas}"
        foreach {theta r d alt x y} $lineNoCommas {break}
        lappend model [list $theta $r $d $alt $x $y]
    }

    # kill the first line
    set model [lreplace $model 0 0]
}

#######################################################################
#
# writeModelToChannel
#
# Write a model to a specified channel
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# channelId : the channel to write to.
#
proc writeModelToChannel {modelVarName channelId} {
    upvar 1 $modelVarName model
    puts $channelId "theta, r, d, alt, x, y"
    foreach point $model {
        foreach {theta r d alt x y} $point {break}
        puts $channelId "$theta, $r, $d, $alt, $x, $y"
    }
}

#######################################################################
#
# readModelFromFile
#
# Read in a model from a specified file
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# filename : the file to read from.
#
proc readModelFromFile {modelVarName filename} {
    upvar 1 $modelVarName model
    set chan [open $filename r]
    readModelFromChannel model $chan
    close $chan
}

#######################################################################
#
# writeModelToFile
#
# Write a model to a specified file
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# filename : the file to write to.
#
proc writeModelToFile {modelVarName filename} {
    upvar 1 $modelVarName model
    set chan [open $filename w]
    writeModelToChannel model $chan
    close $chan
}

#######################################################################
#
# insertPoint
#
# Insert a new point into the model, at the specified index.
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# index : the index of the point to be inserted.  The point already
#         at that index, and all subsequent points, have their index
#         incremented by one.
# point : the point to insert.
#
proc insertPoint {modelVarName index point} {
    upvar 1 $modelVarName model
    set model [linsert $model $index $point]
}

#######################################################################
#
# deletePoint
#
# Remove the point at the specified index.
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# index : the index of the point to be removed.  All subsequent
#         points, have their index decremented by one.
#
proc deletePoint {modelVarName index} {
    upvar 1 $modelVarName model
    set model [lreplace $model $index $index]
}

#######################################################################
#
# replacePoint
#
# Replace the point at the specified index.
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# index : the index of the point to be replaced.
# point : the point which will replace the selected point.
#
proc replacePoint {modelVarName index point } {
    upvar 1 $modelVarName model
    set model [lreplace $model $index $index $point]
}

#######################################################################
#
# subdivideAndDisplace
#
# Create a rugged landscape via a recursive process of
# creating random displacements between each point in the
# model.  Each point is displaced up to [distance] meters
# away from its current location.  The algorithm is applied
# recursively, with a smaller step size.
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# distance: maximum distance to displace each point (in altitude)
#           during the subdivision step.
# levels: how many subdivision steps to perform.
#
# WARNING: each subdivision step doubles the size of the model!
#
# NB: I get very interesting results with a distance=3000 and levels=10.
#
proc subdivideAndDisplace {modelVarName distance levels} {
    puts "distance = $distance, levels = $levels"
    upvar 1 $modelVarName model
    set newmodel {}
    set halfdistance [expr {$distance / 2}]
    set firstpoint [lindex $model 0]

    while {[llength $model] > 0} {
        set thispoint [lindex $model 0]
        if {[llength $model] > 1} {
            set nextpoint [lindex $model 1]
        } else {
            set nextpoint $firstpoint
        }

        # pop the first element off the model
        set model [lreplace $model 0 0]

        foreach {theta1 r1 d1 alt1 x1 y1} $thispoint {break}
        foreach {theta2 r2 d2 alt2 x2 y2} $nextpoint {break}

        if {$theta2 < $theta1} {
            set theta2 [expr {$theta2 + 2 * $::pi}]
        }

        lappend newmodel $thispoint

        set thetaNew [expr {($theta1 + $theta2)/2}]
        set rNew [expr {($r1 + $r2) / 2 + rand() * $distance - $halfdistance}]

        lappend newmodel [fromThetaR $thetaNew $rNew]
    }

    set model $newmodel

    if {$levels > 0} {
        subdivideAndDisplace model \
            [expr {$distance / 2}] [expr {$levels - 1}]
    }
}

#######################################################################
#
# downSample
#
# remove (factor-1)/(factor) of the points in the model and replace
# each point with an average of its neighboring points.
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# factor : the downsampling factor.
#
proc downSample {modelVarName factor} {
    upvar 1 $modelVarName model
    set newmodel {}
    set factorm1 [expr {$factor - 1}]
    if {$factor <= 1} {return}

    # Always keep the first element
    lappend newmodel [lindex $model 0]
    set model [lreplace $model 0 0]

    while {[llength $model] > 0} {
        set count 0
        set theta 0
        set rad 0
        while {($count < $factorm1)&&($count < [llength $model])} {
            set point [lindex $model $count]
            foreach {thetap rp d alt x y} $point {break}
            set theta [expr {$theta + $thetap}]
            set rad [expr {$rad + $rp}]
            incr count
        }
        lappend newmodel [fromThetaR [expr {$theta/$count}] [expr {$rad/$count}]]
        set model [lreplace $model 0 [expr {$count - 1}]]
    }

    set model $newmodel
}

#######################################################################
#
# zeroCrossings
#
# return a vector of all zero crossings of the given model.  If they
# need to be interpolated, they will be.
#
proc zeroCrossings {model} {
    set rv {}
    set lasttheta -1
    set firstpointloaded 0
    foreach point $model {
        foreach {theta r d alt x y} $point {break}
        if {$alt == 0} {
            if {$lasttheta != 0} {
                lappend rv $theta
            }
        } elseif {$firstpointloaded} {
            if {(($alt > 0)&&($lastalt < 0))||(($alt < 0)&&($lastalt > 0))} {
                # Compute the intersection point and store it
                set dalt [expr {$alt - $lastalt}]
                set dtheta [expr {$theta - $lasttheta}]
                set newtheta [expr {($dtheta/$dalt) * (-1.0 * $lastalt) + $lasttheta}]
                lappend rv $newtheta
            }
        }
        set lasttheta $theta
        set lastr $r
        set lastd $d
        set lastalt $alt
        set lastx $x
        set lasty $y
        set firstpointloaded 1
    }
    return $rv
}

# fill in any parts of the landscape which are under the nominal altitude.
# rewrite this
proc fillAllMaria {modelVarName} {
    upvar 1 $modelVarName model
    foreach point $model {
        foreach {theta r d alt x y} $point {break}
        if {$alt > -1} {
            lappend newmodel $point
        }
    }

    set model $newmodel
}

#######################################################################
#
# getLeftIndex
#
# return the index which is either exactly on, or just to the left of,
# a given theta.
# return -1 if we can't find a good index
#
# PARAMETERS:
# model : the model to search
# itheta : the theta to match
# startIndex : begin looking from this index, 0 if unspecified.
#
proc getLeftIndex {model itheta {startIndex 0}} {
    set len [llength $model]
    for {set idx $startIndex} {$idx < $len} {incr idx} {
        foreach {theta r d alt x y} [lindex $model $idx] {break}
        if {$theta > $itheta} {
            return [expr {$idx - 1}]
        }
    }
    return [expr {$idx - 1}]
}

#######################################################################
#
# getRightIndex
#
# return the index which is either exactly on, or just to the right of,
# a given theta.
# return -1 if we can't find a good index
#
# PARAMETERS:
# model : the model to search
# itheta : the theta to match
# startIndex : begin looking from this index, 0 if unspecified.
#
proc getRightIndex {model itheta {startIndex 0}} {
    set len [llength $model]
    for {set idx $startIndex} {$idx < $len} {incr idx} {
        foreach {theta r d alt x y} [lindex $model $idx] {break}
        if {$theta >= $itheta} {
            return $idx
        }
    }
    return -1
}

#######################################################################
#
# getRadiusAt
#
# return the interpolated radius at a given theta, plus the indices
# bracketing it.
#
# PARAMETERS:
# model: the model to search
# theta: the angle
#
# RETURNS: a list in the form:
# {rad index1 index2}
# where
#   rad = the radius at theta
#   index1 = the index of the point to the left of theta
#   index2 = the index of the point to the right of theta.
#
proc getRadiusAt {model theta} {
    set t1 [getLeftIndex $model $theta]
    set t2 [getRightIndex $model $theta]

    if {$t1 == -1 || $t2 == -1} {
        error "Can't find theta $theta"
    }

    set point1 [lindex $model $t1]
    set point2 [lindex $model $t2]

    foreach {theta1 r1 d1 alt1 x1 y1} $point1 {break}
    foreach {theta2 r2 d2 alt2 x2 y2} $point2 {break}

    if {$theta2 == $theta1 || $theta == $theta1} {
        set rad $r1
    } else {
        set rad [expr { $r1 + (($r2 - $r1) / ($theta2 - $theta1)) * ($theta - $theta1) } ]
    }

    return [list $rad $t1 $t2]
}

#######################################################################
#
# getAltAt
#
# return the interpolated altitude at a given theta, plus the indices
# bracketing it.
#
# PARAMETERS:
# model: the model to search
# theta: the angle
#
# RETURNS: a list in the form:
# {alt index1 index2}
# where
#   alt = the altitude at theta
#   index1 = the index of the point to the left of theta
#   index2 = the index of the point to the right of theta.
#
proc getAltAt {model theta} {
    foreach {rad idx1 idx2} [getRadiusAt $model $theta] {break}
    set alt [expr {$rad - $::rMoon}]
    return [list $alt $idx1 $idx2]
}

proc getAverageAlt {model} {
    set altaccum 0
    set npoints 0
    foreach point $model {
        foreach {theta r d alt x y} $point {break}
        set altaccum [expr {$altaccum + $alt}]
        incr npoints
    }
    if {$npoints > 0} {
        return [expr {$altaccum / $npoints}]
    } else {
        return 0
    }
}

#######################################################################
#
# biasModel
#
# Raise or lower the entire model by a certain value
#
# PARAMETERS
# modelVarName: the name of the variable containing the list which
#                is the model.
# biasValue : the number of meters to raise or lower each point by.
#
proc biasModel {modelVarName biasValue} {
    upvar 1 $modelVarName model
    if {$biasValue == 0} {return}
    set newmodel {}
    foreach point $model {
        foreach {theta r d alt x y} $point {break}
        set r [expr {$r + $biasValue}]
        lappend newmodel [fromThetaR $theta $r]
    }
    set model $newmodel
    puts "Done"
}

#######################################################################
#
# scaleModel
#
# scale the model (using altitude)
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# scaleValue : the value to scale altitude by.
#
proc scaleModel {modelVarName scaleValue} {
    upvar 1 $modelVarName model
    if {$scaleValue == 0} {return}
    if {$scaleValue == 1} {return}
    set newmodel {}
    foreach point $model {
        foreach {theta r d alt x y} $point {break}
        set alt [expr {$alt * $scaleValue}]
        lappend newmodel [fromTRadAlt $theta $alt]
    }
    set model $newmodel
    puts "Done"
}

#######################################################################
#
# addFlat
#
# Flatten the region between theta1 and theta2 (using
# average radius of the two endpoints.
#
# PARAMETERS:
# modelVarName : the name of the variable containing the list which
#                is the model.
# theta1 : endpoint 1 of the flattening operation.
# theta2 : endpoint 2 of the flattening operation.
# useAltitude: instead of using the average radius, use a given
#              altitude.
# altitude: use this altitude if useAltitude==1.
#
proc addFlat {modelVarName theta1_in theta2_in {useAltitude 0} {altitude 0} } {
    #puts "useAltitude = $useAltitude, altitude = $altitude"
    upvar 1 $modelVarName model

    if {$theta1_in > $theta2_in} {
        set theta2 $theta1_in
        set theta1 $theta2_in
    } else {
        set theta2 $theta2_in
        set theta1 $theta1_in
    }

    foreach {r1 t11 t12} [getRadiusAt $model $theta1] {break}
    foreach {r2 t21 t22} [getRadiusAt $model $theta2] {break}
    set r [expr {($r1 + $r2) / 2}]
    if {$useAltitude} {
        set point1 [fromTRadAlt $theta1 $altitude]
        set point2 [fromTRadAlt $theta2 $altitude]
    } else {
        set point1 [fromThetaR $theta1 $r]
        set point2 [fromThetaR $theta2 $r]
    }
    set model [lreplace $model $t12 $t21 $point1 $point2]
    puts "done"
}

proc addCrater {modelVarName radius position} {
    upvar 1 $modelVarName model
    set startangle $::pi
    set endangle [expr {2 * $::pi}]
    set anglestep [expr {$::pi/20}]

    set dtheta [expr {$radius / $::rMoon}]
    set lefttheta [expr {$position - $dtheta}]
    set righttheta [expr {$position + $dtheta}]
    foreach {centerradius idx idx} [getRadiusAt $model $position] {break}
    foreach {leftradius idxleft1 idxleft2} [getRadiusAt $model $lefttheta] {break}
    foreach {rightradius idxright1 idxright2} [getRadiusAt $model $righttheta] {break}

    set newmodel [lrange $model 0 $idxleft1]
    set newmodelRight [lrange $model $idxright2 end]
    for {set angle $startangle} {$angle <= $endangle} {set angle [expr {$angle + $anglestep}]} {
        set x [expr {$position + ($radius * cos($angle))/$::rMoon}]
        set y [expr {$centerradius + ($radius * sin($angle))}]

        lappend newmodel [fromThetaR $x $y]
    }

    foreach point $newmodelRight {
        lappend newmodel $point
    }
    set model $newmodel
    puts "done"
}

proc addCanyon {width depth position} {
}

proc addMaria {width position} {
}

# build the mountain over existing terrain
# (i.e. a mountain of height 5 in a canyon of 3 will have a final
# altitude of 2)
proc addMountain {width height position} {
}

proc smooth {factor} {
}

proc getMaxima {model} {
    set min 0
    set max 0
    foreach point $model {
        foreach {t r d alt x y} $point {break}
        if {$alt < $min} {set min $alt}
        if {$alt > $max} {set max $alt}
    }
    return [list $min $max]
}

#######################################################################
#
# Drawing parameters
#
#

# Viewport center in World Coordinates (m)
set ViewCtrX_W 0
set ViewCtrY_W 0

# Viewport center in Screen Coordinates (px)
set ViewCtrX_S 0
set ViewCtrY_S 0

# Viewport width and height in Screen Units (px)
set ViewWidth_S 0
set ViewHeight_S 0

# Viewport Scaling, meters per pixel and pixels per meter
set ViewScale_mpp 0
set ViewScale_ppm 0

# Viewport Rotation: in radians
set ViewRotation 0

# Viewport basis vector uhat, points to viewport right
# Expressed in World Coordinates
set ViewUHatX_W 1
set ViewUHatY_W 0

# Viewport basis vector vhat, points to viewport top
# Expressed in World Coordinates
set ViewVHatX_W 0
set ViewVHatY_W 1

# The side of the longest square which must be used
# to contain the terrain, given that it must be centered
# at (0,0) world coordinates.
set ViewMaxRadius -1

#######################################################################
#
# printViewportParams
#
proc printViewportParams {} {
    puts "ViewCtrX_W     = $::ViewCtrX_W     "
    puts "ViewCtrY_W     = $::ViewCtrY_W     "

    puts "ViewCtrX_S     = $::ViewCtrX_S     "
    puts "ViewCtrY_S     = $::ViewCtrY_S     "

    puts "ViewWidth_S    = $::ViewWidth_S    "
    puts "ViewHeight_S   = $::ViewHeight_S    "

    puts "ViewScale_mpp  = $::ViewScale_mpp  "
    puts "ViewScale_ppm  = $::ViewScale_ppm  "

    puts "ViewRotation   = $::ViewRotation   "

    puts "ViewUHatX_W    = $::ViewUHatX_W    "
    puts "ViewUHatY_W    = $::ViewUHatY_W    "

    puts "ViewVHatX_W    = $::ViewVHatX_W    "
    puts "ViewVHatY_W    = $::ViewVHatY_W    "

    puts "ViewMaxRadius  = $::ViewMaxRadius  "
}

#######################################################################
#
# computeViewportExtent
#
# Given a model, figure out the size of the square, centered at
# world coordinates (0,0), which will hold the model in its
# entirety.
#
# RETURNS
# the length of the square's side.
#
# this routine will also set ::ViewMaxRadius.
#
proc computeViewportExtent {model} {
    set modellen [llength $model]
    set maxdim 0
    foreach point $model {
        foreach {t r d alt x y} $point {break}
        if {$r > $maxdim} {
            set maxdim $r
        }
    }
    set ::ViewMaxRadius $maxdim

    return [expr {2.0 * $maxdim}]
}

#######################################################################
#
# setupViewportScale
#
# Set up the viewport scale such that it will contain the entire model.
#
# PARAMETERS:
# w: the widget to use.
# model : the model to fit into the widget.
#
proc setupViewportScale {w model} {
    if {$::ViewMaxRadius == -1} {
        computeViewportExtent $model
    }

    set ::ViewCtrX_W 0
    set ::ViewCtrY_W 0
    set ::ViewWidth_S [winfo width $w ]
    #puts "setupViewportScale: width = $::ViewWidth_S"
    set ::ViewHeight_S [winfo height $w ]
    #puts "setupViewportScale: height = $::ViewHeight_S"
    set ::ViewCtrX_S [expr {$::ViewWidth_S * 0.5} ]
    set ::ViewCtrY_S [expr {$::ViewHeight_S * 0.5} ]
    if { $::ViewWidth_S < $::ViewHeight_S } {
        set minDim $::ViewWidth_S
    } else {
        set minDim $::ViewHeight_S
    }

    if { ($minDim <= 0.0)||($::ViewMaxRadius <= 0.0)} {
        # TODO: Handle this better.
        puts "Error: Can't compute viewport scale"
        return
    }

    set ::ViewScale_mpp [expr {($::ViewMaxRadius * 2.0) / $minDim} ]
    #puts "::ViewScale_mpp = $::ViewScale_mpp"
    set ::ViewScale_ppm [expr {1.0 / $::ViewScale_mpp}]
    #puts "::ViewScale_ppm = $::ViewScale_ppm"
}

#######################################################################
#
# setViewAngle
#
proc setViewAngle {angleRadians} {
    set ::ViewRotation $angleRadians
    set C [expr {cos($angleRadians)}]
    set S [expr {sin($angleRadians)}]

    set ::ViewUHatX_W $C
    set ::ViewUHatY_W $S
    set ::ViewVHatX_W [expr {-1.0 * $S}]
    set ::ViewVHatY_W $C
}


#######################################################################
#
# setupInitialViewport
#
# Set up the viewport at the beginning of the session, after a load.
#
proc setupInitialViewport {w model} {
    set ::ViewMaxRadius -1
    setupViewportScale $w $model
    setViewAngle 0
}

#######################################################################
#
# rescaleViewport
#
# Maintaining the same extents in world coordinates, rescale the view
# as the size of the viewport in screen coordinates changes.
#
proc rescaleViewport {w} {

    set NewViewWidth_S [winfo width $w ]
    set NewViewHeight_S [winfo height $w ]
    if {$NewViewWidth_S < $NewViewHeight_S} {
        set newMinDim $NewViewWidth_S
    } else {
        set newMinDim $NewViewHeight_S
    }

    if { $::ViewWidth_S < $::ViewHeight_S } {
        set minDim $::ViewWidth_S
    } else {
        set minDim $::ViewHeight_S
    }

    if { ($minDim <= 0.0)||($newMinDim <= 0.0)||($::ViewScale_mpp <= 0.0)} {
        # TODO: Handle this better.
        puts "Error: Can't compute viewport scale"
        return
    }

    set ::ViewScale_mpp [expr {($::ViewScale_mpp * $minDim) / $newMinDim} ]
    #puts "::ViewScale_mpp = $::ViewScale_mpp"
    set ::ViewScale_ppm [expr {1.0 / $::ViewScale_mpp}]
    #puts "::ViewScale_ppm = $::ViewScale_ppm"

    set ::ViewWidth_S $NewViewWidth_S
    set ::ViewHeight_S $NewViewHeight_S
    set ::ViewCtrX_S [expr {$::ViewWidth_S * 0.5} ]
    set ::ViewCtrY_S [expr {$::ViewHeight_S * 0.5} ]

}

#######################################################################
#
# worldToScreen
#
# transform a point from world coordinates to screen coordinates.
#
proc worldToScreen {x y} {
    # Compute D, which is X - viewport origin
    set dx [expr {$x - $::ViewCtrX_W}]
    set dy [expr {$y - $::ViewCtrY_W}]

    # Compute U = D dot Uhat
    set U [expr {$dx * $::ViewUHatX_W + $dy * $::ViewUHatY_W}]
    # Compute V = D dot Vhat
    set V [expr {$dx * $::ViewVHatX_W + $dy * $::ViewVHatY_W}]

    # compute u = U, scaled and translated.
    set u [expr {$U * $::ViewScale_ppm + $::ViewCtrX_S} ]
    # compute v = V, scaled and translated
    set v [expr {$::ViewCtrY_S - $V * $::ViewScale_ppm} ]

    return [list $u $v ]
}

#######################################################################
#
# screenToWorld
#
# transform a point from screen coordinates to world coordinates
#
proc screenToWorld {x y} {
    # Compute U
    set U [expr { ($x - $::ViewCtrX_S) * $::ViewScale_mpp} ]
    # Compute V
    set V [expr { ($::ViewCtrY_S - $y) * $::ViewScale_mpp} ]

    # Compute dx
    set dx [expr { $U * $::ViewUHatX_W + $V * $::ViewVHatX_W } ]
    # Compute dy
    set dy [expr { $U * $::ViewUHatY_W + $V * $::ViewVHatY_W } ]

    set x [expr {$dx + $::ViewCtrX_W}]
    set y [expr {$dy + $::ViewCtrY_W}]

    return [list $x $y]
}

#######################################################################
#
# centerAtScreenXY
#
# recenter the viewport at the given screen coordinates
#
proc centerAtScreenXY {sx sy} {
    foreach {wx wy} [screenToWorld $sx $sy] {}
    set ::ViewCtrX_W $wx
    set ::ViewCtrY_W $wy
}

#######################################################################
#
# zoom
#
# multiply the pixels per meter by a given factor.
# >1 zooms in.
# <1 zooms out.
#
proc zoom {factor} {
    #puts "zoom, factor = $factor"

    if {$factor == 0} {
        return
    }

    set newZoom [expr {$::ViewScale_ppm * $factor}]
    set ::ViewScale_ppm $newZoom
    set ::ViewScale_mpp [expr {1.0 / $newZoom}]
}

#######################################################################
#
# ptInView
#
# returns 1 if x and y are within the bounds given
#
proc ptInView {x y xmin xmax ymin ymax} {
    if {$x < $xmin} { return 0 }
    if {$x > $xmax} { return 0 }
    if {$y < $ymin} { return 0 }
    if {$y > $ymax} { return 0 }

    return 1
}

#######################################################################
#
# drawXY
#
proc drawXY {w model} {
    $w delete all

    set modellen [llength $model]

    if {$modellen == 0} {
        return
    }

    set XMin 0
    set XMax [winfo width $w ]
    set YMin 0
    set YMax [winfo height $w]

    # Draw the 0-degree line
    foreach {x1 y1} [worldToScreen 0 0] {}
    foreach {x2 y2} [worldToScreen $::rMoon 0] {}
    $w create line $x1 $y1 $x2 $y2 -fill "blue"

    # Draw the datum circle
    set moonRadiusPixels [expr {$::rMoon * $::ViewScale_ppm}]
    foreach {originx originy} [worldToScreen 0 0] {}
    set x1 [expr {$originx - $moonRadiusPixels}]
    set y1 [expr {$originy - $moonRadiusPixels}]
    set x2 [expr {$originx + $moonRadiusPixels}]
    set y2 [expr {$originy + $moonRadiusPixels}]
    $w create oval $x1 $y1 $x2 $y2 -outline "blue"

    # Draw the center lines
    set XMiddle [expr {$XMax * 0.5}]
    set YMiddle [expr {$YMax * 0.5}]
    $w create line 0 $YMiddle $XMax $YMiddle -fill "red"
    $w create line $XMiddle 0 $XMiddle $YMax -fill "red"

    ###########################
    # Draw the terrain contours
    set idx 0
    set x2 0
    set y2 0
    set firstx 0
    set firsty 0
    set pt1inView 0
    set pt2inView 0
    foreach point $model {
        foreach {theta2 rr dd alt2 xx yy} $point {}
        set x1 $x2
        set y1 $y2
        set pt1inView [ptInView $x1 $y1 $XMin $XMax $YMin $YMax]
        foreach {x2 y2} [worldToScreen $xx $yy] {}
        set pt2inView [ptInView $x2 $y2 $XMin $XMax $YMin $YMax]
        if {$idx == 0} {
            set firstx $x2
            set firsty $y2
        } else {
            if {$pt1inView || $pt2inView} {
                $w create line $x1 $y1 $x2 $y2 -fill "white"
            }
        }

        if {$::ViewScale_mpp < 100} {

            set tagname "tpoint"
            append tagname $idx

            $w create rectangle \
                [expr {$x2 - 5}] [expr {$y2 - 5}] \
                [expr {$x2 + 5}] [expr {$y2 + 5}] \
                -fill "green" -tag $tagname
        }

        incr idx
    }
    $w create line $x2 $y2 $firstx $firsty -fill "white"

}

#######################################################################
#
# pointIndexFromTag
#
# given a tag from the display widget's current tag, determine the
# index of the terrain point.  Return -1 if there is no index to be
# found.
#
proc pointIndexFromTag {tag} {
    set idx 0

    if {$tag == ""} {
        return -1
    }

    set count [scan $tag "tpoint%d" idx]
    if {$count == 0} {
        set idx -1
    }
    return $idx
}


#######################################################################
#
# drawThetaAltitude
#
# This draw routine is strictly for theta vs altitude.
#
# proc drawThetaAltitude {w model} {
#     global verticalscalefactor horizontalscalefactor hdisp wdisp ycenter
#     $w delete all
#
#     set modellen [llength $model]
#     #puts "modellen = $modellen"
#
#     # Get dimensions of the window
#     set hdisp [winfo height $w ]
#     set wdisp [winfo width $w ]
#
#     # Draw the grey line across the center
#     set ycenter [expr {$hdisp / 2}]
#     $w create line 0 $ycenter $wdisp $ycenter -fill "darkgrey"
#
#     if {$model != "x"} {
#         #puts "Getting extents..."
#         foreach {minalt maxalt} [getMaxima $model] {break}
#         #puts "Got $minalt, $maxalt"
#         set minalt [expr {abs($minalt)}]
#         if {$maxalt < $minalt} {
#             set maxalt $minalt
#         }
#         if {$maxalt < 100} {
#             set maxalt 100
#         } else {
#             set maxalt [expr {$maxalt * 1.1}]
#         }
#         set verticalscalefactor [expr {-1.0 * $hdisp / (2 * $maxalt)}] ; # pixels/m
#         set horizontalscalefactor [expr {$wdisp / (2 * $::pi)}] ; # pixels/radian
#
#         set idx 0
#         set theta2 0
#         set alt2 0
#         foreach point $model {
#             #puts "idx = $idx"
#             set theta1 $theta2
#             set alt1 $alt2
#             foreach {theta2 rr dd alt2 xx yy} $point {}
#             if {$idx != 0} {
#
#                 set x1 [expr {$theta1 * $horizontalscalefactor}]
#                 set x2 [expr {$theta2 * $horizontalscalefactor}]
#                 set y1 [expr {$ycenter + $alt1 * $verticalscalefactor}]
#                 set y2 [expr {$ycenter + $alt2 * $verticalscalefactor}]
#
#                 $w create line $x1 $y1 $x2 $y2 -fill "white"
#             }
#             incr idx
#         }
#     }
# }

proc draw {w model} {
    #drawThetaAltitude $w $model
    drawXY $w $model
}

# proc canvasCoordsToThetaAlt {w x y} {
#     global verticalscalefactor horizontalscalefactor hdisp wdisp ycenter
#
#     if {![info exist verticalscalefactor]} {
#         return {0 0}
#     }
#
#     set theta [expr {$x / $horizontalscalefactor}]
#     set alt [expr {($y - $ycenter) / $verticalscalefactor }]
#
#     return [list $theta $alt]
# }

#######################################################################
#
# create the actual display widgets
#
catch { destroy .display }
catch { destroy .toplabel }
label .toplabel -bg black -fg white -justify left -text {Welcome to Terrain Modeler}
canvas .display -bg black
canvas .buttons -bg black -height 20

set buttonsGridCmd "grid "
for {set bnum 1} {$bnum <= 10} {incr bnum} {
    button .buttons.btn$bnum -text "Button $bnum" -command "puts \"Button $bnum\""
    append buttonsGridCmd ".buttons.btn$bnum "
}
append buttonsGridCmd "-sticky nsew"

# Assign the widgets to the grid
#eval {grid .buttons.btn1 .buttons.btn2 .buttons.btn3 .buttons.btn4 .buttons.btn5 -sticky nsew}
eval $buttonsGridCmd

grid .toplabel -padx 2 -pady 1 -sticky nsew
grid .display -padx 2 -pady 1 -sticky nsew
grid .buttons -padx 2 -pady 1 -sticky nsew
grid columnconfigure . 0 -weight 1
grid rowconfigure . 1 -weight 100
grid rowconfigure . 2 -weight 0



# Ensure that the display widget gets mouse wheel events.
focus .display

#######################################################################
#
# Event bindings
#
bind .display <Configure> {
    rescaleViewport .display
    redraw
}
#######################################################################
#
# Mouse Motion
#
# when the mouse is moving, it is relaying information to the top bar
# about the current location of the cursor.
# In addition, if the user is dragging a point, it will be displayed.
#
bind .display <Motion> {
    #set theta_alt [canvasCoordsToThetaAlt .display %x %y]
    #foreach {theta alt} $theta_alt {}
    #.toplabel configure -text "theta,alt = {$theta_alt}"

    foreach {wx wy} [screenToWorld %x %y] {}
    set long [expr {atan2($wy,$wx)}]
    if {$long < 0} {
        set long [expr {$long + $::pi_times_2}]
    }
    set longdeg [expr {$long * 57.29577951}]
    set alt [expr {sqrt($wx*$wx + $wy*$wy) - $::rMoon} ]

    if {$::DragPointIndex != -1} {
        set dragStatus "(dragging Point $::DragPointIndex)"
        .display coords $::DragPointTag [list [expr {%x - 5}] [expr {%y - 5}] [expr {%x + 5}] [expr {%y + 5}]]
    } else {
        set dragStatus ""
    }

    set txt [ format \
      {X = %%3d     Y = %%3d     x = %%0.3f     y = %%0.3f    long = %%0.5f (%%0.5f)     alt = %%0.3f     scale = %%0.3f m/pix %%s} \
      %x %y $wx $wy $long $longdeg $alt $::ViewScale_mpp $dragStatus ]
    set txt "$txt\nLeftButton:Info    MiddleButton:CenterViewpoint    RightButton:RotateCorrect    PgUp:ZoomIn    PgDn:ZoomOut    Space:InsertPt    Del:DeletePt"

    .toplabel configure -text $txt

}

#######################################################################
#
# Variables to support drag and drop editing of terrain point.
#
set ::DragStartX 0
set ::DragStartY 0
set ::DragPointIndex -1
set ::DragPointTag ""

#######################################################################
#
# Release Left Button
#
# release a dragged point in its new location.
#
bind .display <ButtonRelease-1> {
    #set theta_alt [canvasCoordsToThetaAlt .display %x %y]
    #puts "theta,alt = {$theta_alt}"

    if {$::DragPointIndex != -1} {
        set dragDist [expr {abs(%x - $::DragStartX) + abs(%y - $::DragStartY)}]
        if {$dragDist > 5} {
            foreach {wx wy} [screenToWorld %x %y] {}
            set newpoint [fromXY $wx $wy]
            replacePoint ::M $::DragPointIndex $newpoint
            redraw
        }
    }

    set ::DragPointIndex -1
    set ::DragPointTag ""

    return ""
}

#######################################################################
#
# Press Left Button
#
# print information about the point under the cursor, and begin
# dragging it
#
bind .display <ButtonPress-1> {
    foreach {wx wy} [screenToWorld %x %y] {}
    set long [expr {atan2($wy,$wx)}]
    if {$long < 0} {
        set long [expr {$long + $::pi_times_2}]
    }
    set longdeg [expr {$long * 57.29577951}]
    set alt [expr {sqrt($wx*$wx + $wy*$wy) - $::rMoon} ]
    puts "x = $wx   y = $wy   long = $long ($longdeg deg)  alt = $alt"

    set mytag [lindex [.display gettags current] 0]
    set pointIndex [pointIndexFromTag $mytag]
    puts "point index = $pointIndex"

    set ::DragStartX %x
    set ::DragStartY %y
    set ::DragPointIndex $pointIndex
    set ::DragPointTag $mytag
    return ""
}

#######################################################################
#
# Middle Button
#
# Pressing the middle button will center the viewport at the mouse
# pointer location.
#
bind .display <ButtonPress-2> {
    centerAtScreenXY %x %y
    redraw
}

#######################################################################
#
# Right Button
#
# Rearrange the display angle to be correct for the location.
#
bind .display <ButtonPress-3> {
    foreach {wx wy} [screenToWorld %x %y] {}
    set dispang [expr {atan2($wy,$wx)} - $::pi_over_2]
    setViewAngle $dispang
    redraw
}

bind .display <Double-Button-1> {
    puts "Double-Button-1"
}

#######################################################################
#
# Mouse Wheel (Windows only)
#
# Mouse wheel Zooms in (up) and out (down)
#
bind .display <MouseWheel> {
    puts "MouseWheel %D"

    set wheel [expr {abs(%D)}]

    if {%D > 0} {
        set mul 1.1
    } else {
        set mul 0.9
    }

    set factor 1.0

    while {$wheel > 0} {
        set factor [expr {$factor * $mul}]
        set wheel [expr {$wheel - 120}]
    }

    zoom $factor
    redraw
}

#######################################################################
#
# PgUp
#
# PgUp key zooms the viewport in.
#
bind .display <KeyRelease-KP_Prior> {
    zoom 1.1
    redraw
}

#######################################################################
#
# PgDn
#
# PgDn key zooms the viewport out.
#
bind .display <KeyRelease-KP_Next> {
    zoom 0.9
    redraw
}

#######################################################################
#
# Enable this to see the key symbol for a new key you want to add
# a binding for.
#
bind .display <KeyRelease> { puts "keysym = %K keycode = %k" }

#######################################################################
#
# Space Bar
#
# Insert a point where the mouse is pointing.
#
bind .display <KeyRelease-space> {
    puts "Inserting point..."

    foreach {wx wy} [screenToWorld %x %y] {}
    set point [fromXY $wx $wy]

    foreach {theta r d alt x y} $point {}

    set index [getRightIndex $::M $theta]

    if {$index != -1} {
        insertPoint ::M $index $point
        redraw
    }
}

#######################################################################
#
# Backspace and Delete
#
# Delete the point under the mouse.
#
proc deletePointUnderMouse {} {
    set mytag [lindex [.display gettags current] 0]
    set pointIndex [pointIndexFromTag $mytag]
    puts "point index = $pointIndex"

    if {$pointIndex != -1} {
        puts "deleting point $pointIndex"
        deletePoint ::M $pointIndex
        redraw
    }
}

bind .display <KeyRelease-BackSpace> { deletePointUnderMouse }

bind .display <KeyRelease-Delete> { deletePointUnderMouse }

#######################################################################
#
# redraw
#
# Issue this command from the console to redraw the display.
#
proc redraw {} {draw .display $::M}

#######################################################################
#
# resetView
#
# Issue this command from the console to reset the display to its
# its original view.
#
proc resetView {} {
    setupInitialViewport .display $::M
    redraw
    printViewportParams
}

#######################################################################
#
# reload
#
# Issue this command from the console to load the terrain file and
# begin editing.
#
proc reload {} {
    readModelFromFile ::M "terrain001.csv"
    resetView
}

######################################################################
#
# 
proc save {} {
    writeModelToFile ::M "terrain001.csv"
}

proc toRadians {degrees} {
    set radians [expr {$degrees/57.2957795132}]
}

proc rdrAlt { latDegrees dtmAltitude } {
    set latRadians [toRadians $latDegrees]
    set terrainAlt [lindex [getAltAt $::M $latRadians] 0]
    return [expr {$dtmAltitude - $terrainAlt}]
}



tkwait visibility .display
reload
