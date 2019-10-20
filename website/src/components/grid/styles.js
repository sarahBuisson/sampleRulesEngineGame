const styles = {
    grid: (size) => {
        return {
            display: 'grid',
            gridTemplateColumns: `repeat(${size},20px)`,
            gridTemplateRows: '20px'
        }
    },
    gridrow: {
        display: 'grid',
        gridTemplateColumns: '20px'


    }


}

export default styles
