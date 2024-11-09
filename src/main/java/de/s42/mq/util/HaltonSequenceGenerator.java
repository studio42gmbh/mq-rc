/*
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.mq.util;

/**
 *
 * @author Benjamin Schiller
 */
public class HaltonSequenceGenerator
{

	/**
	 * The first 40 primes.
	 */
	private static final int[] PRIMES = {
		2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67,
		71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139,
		149, 151, 157, 163, 167, 173
	};

	/**
	 * The optimal weights used for scrambling of the first 40 dimension.
	 */
	private static final int[] WEIGHTS = {
		1, 2, 3, 3, 8, 11, 12, 14, 7, 18, 12, 13, 17, 18, 29, 14, 18, 43, 41,
		44, 40, 30, 47, 65, 71, 28, 40, 60, 79, 89, 56, 50, 52, 61, 108, 56,
		66, 63, 60, 66
	};

	/**
	 * Space dimension.
	 */
	private final int dimension;

	/**
	 * The current index in the sequence.
	 */
	private int count;

	/**
	 * The base numbers for each component.
	 */
	private final int[] base;

	/**
	 * The scrambling weights for each component.
	 */
	private final int[] weight;

	/**
	 * Construct a new Halton sequence generator for the given space dimension.
	 *
	 * @param dimension the space dimension
	 * @throws IllegalArgumentException if the space dimension is outside the allowed range of [1, 40]
	 */
	public HaltonSequenceGenerator(final int dimension)
	{
		this(dimension, PRIMES, WEIGHTS);
	}

	/**
	 * Construct a new Halton sequence generator with the given base numbers and weights for each dimension. The length
	 * of the bases array defines the space dimension and is required to be &gt; 0.
	 *
	 * @param dimension the space dimension
	 * @param bases the base number for each dimension, entries should be (pairwise) prime, may not be null
	 * @param weights the weights used during scrambling, may be null in which case no scrambling will be performed
	 * @throws NullPointerException if base is null
	 * @throws IllegalArgumentException if the space dimension is outside the range [1, len], where len refers to the
	 * length of the bases array
	 * @throws IllegalArgumentException if weights is non-null and the length of the input arrays differ
	 */
	public HaltonSequenceGenerator(final int dimension, final int[] bases, final int[] weights)
	{

		//MathUtils.checkNotNull(bases);
		//MathUtils.checkRangeInclusive(dimension, 1, bases.length);

		/*if (weights != null && weights.length != bases.length) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                                                       weights.length, bases.length);
            }*/
		this.dimension = dimension;
		this.base = bases.clone();
		this.weight = weights == null ? null : weights.clone();
		count = 0;
	}

	public double[] nextVector()
	{
		final double[] v = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			int index = count;
			double f = 1.0 / base[i];

			int j = 0;
			while (index > 0) {
				final int digit = scramble(i, j, base[i], index % base[i]);
				v[i] += f * digit;
				index /= base[i]; // floor( index / base )
				f /= base[i];
			}
		}
		count++;
		return v;
	}

	/**
	 * Performs scrambling of digit {@code d_j} according to the formula:
	 * <pre>
	 *   ( weight_i * d_j ) mod base
	 * </pre> Implementations can override this method to do a different scrambling.
	 *
	 * @param i the dimension index
	 * @param j the digit index
	 * @param b the base for this dimension
	 * @param digit the j-th digit
	 * @return the scrambled digit
	 */
	protected int scramble(final int i, final int j, final int b, final int digit)
	{
		return weight != null ? (weight[i] * digit) % b : digit;
	}

	/**
	 * Skip to the i-th point in the Halton sequence.
	 * <p>
	 * This operation can be performed in O(1).
	 *
	 * @param index the index in the sequence to skip to
	 * @return the i-th point in the Halton sequence
	 * @throws IllegalArgumentException if index &lt; 0
	 */
	public double[] skipTo(final int index)
	{
		count = index;
		return nextVector();
	}

	/**
	 * Returns the index i of the next point in the Halton sequence that will be returned by calling
	 * {@link #nextVector()}.
	 *
	 * @return the index of the next point
	 */
	public int getNextIndex()
	{
		return count;
	}

}
