package simulation.imgloader;

import java.io.File;
import java.util.ArrayList;

import mpicbg.spim.data.SpimData;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.XmlIoSpimData;
import mpicbg.spim.data.legacy.LegacyImgLoaderWrapper;
import mpicbg.spim.data.registration.ViewRegistrations;
import mpicbg.spim.data.sequence.ImgLoader;
import mpicbg.spim.data.sequence.MissingViews;
import mpicbg.spim.data.sequence.SequenceDescription;
import mpicbg.spim.data.sequence.TimePoints;
import mpicbg.spim.data.sequence.ViewSetup;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import simulation.SimulateBeads;

public class SimulatedBeadsImgLoader extends LegacyImgLoaderWrapper< UnsignedShortType, LegacySimulatedBeadsImgLoader >
{
	public SimulatedBeadsImgLoader( final SimulateBeads sb )
	{
		super( new LegacySimulatedBeadsImgLoader( sb ) );
	}

	public SimulateBeads getSimulateBeads() { return legacyImgLoader.getSimulateBeads(); }

	@Override
	public String toString() {
		return legacyImgLoader.toString();
	}

	public static void save( final SpimData spimData, final String xmlFilename ) throws SpimDataException
	{
		XmlIoSpimData xml = new XmlIoSpimData();
		xml.save( spimData, xmlFilename );
	}

	public static SpimData spimdataExample( final int[] angles )
	{
		final int axis = 0;
		final int numPoints = 1000;
		final double[] sigma = new double[]{ 1, 1, 3 };
		final FinalInterval range = new FinalInterval( 512, 512, 200 );

		return spimdataExample( angles, axis, numPoints, sigma, range );
	}

	public static SpimData spimdataExample()
	{
		final int[] angles = new int[]{ 0, 45, 90, 135 };

		return spimdataExample( angles );
	}

	public static SpimData spimdataExample( final int[] angles, final int axis, final int numPoints, final double[] sigma, final Interval range )
	{
		final TimePoints timepoints = LegacySimulatedBeadsImgLoader.createTimepoints();
		final ArrayList< ViewSetup > setups = LegacySimulatedBeadsImgLoader.createViewSetups( angles, axis, range );
		final MissingViews missingViews = null;

		final SimulateBeads sb = new SimulateBeads( angles, axis, numPoints, range, range, sigma );

		final SequenceDescription sequenceDescription = new SequenceDescription( timepoints, setups, null, missingViews );
		final ImgLoader imgLoader = new SimulatedBeadsImgLoader( sb );
		sequenceDescription.setImgLoader( imgLoader );

		// get the minimal resolution of all calibrations
		final double minResolution = 1.0;

		final ViewRegistrations viewRegistrations = LegacySimulatedBeadsImgLoader.createViewRegistrations( sequenceDescription.getViewDescriptions(), minResolution );

		// finally create the SpimData itself based on the sequence description and the view registration
		final SpimData spimData = new SpimData( new File( "" ), sequenceDescription, viewRegistrations );

		return spimData;
	}

	public static void main( String[] args ) throws SpimDataException
	{
		final SpimData d = SimulatedBeadsImgLoader.spimdataExample();
		save( d, new File( "simulated.xml" ).getAbsolutePath() );
		System.out.println( "done" );
	}
}
