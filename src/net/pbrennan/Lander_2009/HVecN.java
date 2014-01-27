package net.pbrennan.Lander_2009;

/**
 * A vector class which allows the user to specify its size at construction time.
 */
public class HVecN
    implements Cloneable
{
    public int size;
    public double[] vec;

    public HVecN(int _size)
    {
        vec = new double[_size];
        size = _size;
        for (int i=0 ; i<_size ; ++i)
        {
            vec[i] = 0.0;
        }
    }

    /**
     * copy constructor
     * 
     * @param other the vector to be copied.
     */
    public HVecN(HVecN other)
    {
        size = other.size;
        vec = new double[size];
        for (int i=0; i<size ; ++i)
            this.vec[i] = other.vec[i];
    }

    /**
     * 2d value constructor. Creates a vector of dimension 2 and populates it.
     * 
     * @param x
     * @param y
     */
    public HVecN(double x, double y)
    {
        size = 2;
        vec = new double[size];
        vec[0] = x;
        vec[1] = y;
    }

    /**
     * 3d value constructor. Creates a vector of dimension 3 and populates it.
     * 
     * @param x
     * @param y
     * @param z
     */
    public HVecN(double x, double y, double z)
    {
        size = 3;
        vec = new double[size];
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
    }

    /**
     * Copy operator.
     * 
     * @param other the vector to be copied.
     */
    public void copy(HVecN other)
    {
        size = other.size;
        for (int i=0; i<size ; ++i)
            vec[i] = other.vec[i];
    }

    /**
     * Create a new vector from this, add b and return a new vector as the result.
     * The existing vector will not be modified.
     * @param b the vector to be added to this.
     * @return the resulting vector.
     */
    public HVecN add(HVecN b)
    {
        // TODO: Exception out if the sizes are wrong
        HVecN rv = new HVecN(this);
        rv.sum(b);
        return rv;
    }

    /**
     * Sum b to this and return the result.
     * @param b the vector to be added to this.
     * @return the resulting vector.
     */
    public HVecN sum(HVecN b)
    {
        // TODO: Exception out if the sizes are wrong
        int thesize = size;
        for (int i=0 ; i<thesize ; ++i)
        {
            this.vec[i] += b.vec[i];
        }

        return this;
    }

    // Subtract b from this and return a new vector as the result.
    public HVecN sub(HVecN b)
    {
        HVecN rv = new HVecN(this);
        rv.negsum(b);
        return rv;
    }

    public HVecN negsum(HVecN b)
    {
        // TODO: Exception out if the sizes are wrong
        int thesize = size;
        for (int i=0 ; i<thesize ; ++i)
        {
            this.vec[i] -= b.vec[i];
        }

        return this;
    }

    // multiply this vector by a scalar factor and
    // return a new vector as the result.
    public HVecN mul(double factor)
    {
        HVecN rv = new HVecN(this);
        rv.scale(factor);
        return rv;
    }

    // scale this vector in place.
    public HVecN scale(double factor)
    {
        int thesize = size;
        for (int i=0; i<thesize ; ++i)
        {
            this.vec[i] *= factor;
        }
        return this;
    }

    public double dot(HVecN b)
    {
        double rv = 0.0;
        // TODO: Exception out if the sizes are wrong
        int thesize = size;
        for (int i=0 ; i<thesize ; ++i)
        {
            rv += this.vec[i] * b.vec[i];
        }
        return rv;
    }

    public double magnitude()
    {
        double accum = 0.0;
        for (int i=0 ; i<size ; ++i) {
            accum += vec[i] * vec[i];
        }
        return Math.sqrt(accum);
    }

    // normalize this vector in place.
    public void normalize()
    {
        double mag = magnitude();

        try
        {
            for (int i=0 ; i<size ; ++i)
            {
                vec[i] /= mag;
            }
        }
        catch (ArithmeticException e)
        {
            for (int i=0 ; i<size ; ++i)
            {
                vec[i] = 0.0;
            }
        }
    }

    // create a new vector as a copy of this
    // one, normalize it and return
    public HVecN normalized()
    {
        HVecN retVal = new HVecN(this);
        retVal.normalize();
        return retVal;
    }

    public String toString()
    {
        //System.out.println("in toString... size = " + size + " vec = " + vec[0] + ", " + vec[1] + ", " + vec[2] + ", " + vec[3]);
        String rv = "[ ";
        for (int i=0 ; i<size ; ++i)
        {
            rv += (vec[i]);
            if (i<(size-1))
                rv += ", ";
        }
        rv += " ]";
        return rv;
    }
}